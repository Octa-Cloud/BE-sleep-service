// com.project.sleep.domain.domain.service.SleepArchiveService.java
package com.project.sleep.domain.domain.service;

import com.project.sleep.domain.infra.kafka.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.project.sleep.domain.domain.entity.FailureCode.*;

/**
 * 유저별 수면 데이터의 "아카이브 → 삭제" 및 "복구" 서비스.
 *
 * 핵심 정책
 * - 아카이브 컬렉션(*_archive)에는 TTL 인덱스가 적용되어 있어 일정 기간 후 자동 파기.
 * - 원본 → 아카이브는 save(upsert)로 수행하여 _id 충돌을 회피하고 중복을 방지.
 * - 단건/다건 컬렉션을 분리 처리하여 I/O/쿼리 효율 확보.
 * - 비즈니스 실패는 BusinessRuleException으로 신호 → 컨슈머가 FAIL reply + 터미널 처리.
 *
 * 컨슈머 연계
 * - onDeleteCommand() 트랜잭션에서 이 서비스 호출 성공 → SUCCESS reply
 * - 이 서비스가 BusinessRuleException throw → FAIL reply + TERMINAL_FAIL 마킹(재처리 금지)
 */
@Service
@RequiredArgsConstructor
public class SleepArchiveService {

    private final MongoTemplate mongo;

    // 정책 파라미터(필요하면 설정으로 분리)
    private final long TOO_MANY_DOCS_THRESHOLD = 100_000_000L;    // 과도 폭주 보호(선택)
    private final boolean USER_NOT_FOUND_AS_SUCCESS = true;       // 삭제할게 없어도 성공으로 볼지

    /** 원본 → 아카이브 이동 & 원본 삭제(일관성 체크 포함). */
    public void archiveAndDeleteAllOfUser(Long userNo) {
        Instant now = Instant.now();

        // 0) 운영 보호 스위치(점검/락)
        if (isDeleteGuardOn()) {
            throw new BusinessRuleException(DELETE_GUARD_ENABLED, "delete guard is ON");
        }

        // (선택) 과도 문서 수 보호
        long total = countAll(userNo);
        if (total > TOO_MANY_DOCS_THRESHOLD) {
            throw new BusinessRuleException(TOO_MANY_DOCS, "docs=" + total + " exceeds threshold");
        }

        // 실제 이동/삭제 누적
        long moved = 0, removed = 0;

        var o1 = moveOne(userNo, "sleep_goal", now);         moved += o1[0]; removed += o1[1];
        var o2 = moveOne(userNo, "total_sleep_record", now); moved += o2[0]; removed += o2[1];

        var m1 = moveMany(userNo, "daily_report", now);          moved += m1[0]; removed += m1[1];
        var m2 = moveMany(userNo, "daily_sleep_record", now);    moved += m2[0]; removed += m2[1];
        var m3 = moveMany(userNo, "periodic_report", now);       moved += m3[0]; removed += m3[1];

        // 삭제 대상 없음 처리
        if (moved == 0 && removed == 0) {
            if (USER_NOT_FOUND_AS_SUCCESS) {
                return; // no-op 성공
            } else {
                throw new BusinessRuleException(USER_NOT_FOUND_ACTIVE, "no active docs for user " + userNo);
            }
        }

        // 이동 수 != 삭제 수 → 일관성 위반
        if (moved != removed) {
            throw new BusinessRuleException(CONSISTENCY_MISMATCH,
                    "moved=" + moved + ", removed=" + removed);
        }
    }

    /** 아카이브 → 원본 복구(보상). */
    public void restoreAllOfUser(Long userNo) {
        long restored = 0, removedFromArchive = 0;

        var r1 = restoreOne(userNo, "sleep_goal");                restored += r1[0]; removedFromArchive += r1[1];
        var r2 = restoreOne(userNo, "total_sleep_record");        restored += r2[0]; removedFromArchive += r2[1];

        var a1 = restoreMany(userNo, "daily_report");             restored += a1[0]; removedFromArchive += a1[1];
        var a2 = restoreMany(userNo, "daily_sleep_record");       restored += a2[0]; removedFromArchive += a2[1];
        var a3 = restoreMany(userNo, "periodic_report");          restored += a3[0]; removedFromArchive += a3[1];

        // 복구할 소스 자체가 없음
        if (restored == 0 && removedFromArchive == 0) {
            throw new BusinessRuleException(COMPENSATE_SOURCE_MISSING,
                    "no documents in *_archive for user " + userNo);
        }
    }

    // ===== 내부 헬퍼 =====

    /** 유저 관련 모든 원본 컬렉션 총합 */
    private long countAll(Long userNo) {
        long c = 0;
        c += mongo.count(idOrUserNoEq(userNo), "sleep_goal");
        c += mongo.count(idOrUserNoEq(userNo), "total_sleep_record");
        c += mongo.count(userNoEq(userNo), "daily_report");
        c += mongo.count(userNoEq(userNo), "daily_sleep_record");
        c += mongo.count(userNoEq(userNo), "periodic_report");
        return c;
    }

    /** 단건 이동: 원본 → *_archive (moved, removed 반환) */
    private long[] moveOne(Long userNo, String coll, Instant now) {
        var q = idOrUserNoEq(userNo);
        Document doc = mongo.findOne(q, Document.class, coll);
        if (doc != null) {
            doc.put("_archivedAt", now);
            mongo.save(doc, coll + "_archive"); // _id 유지(upsert)
            long del = mongo.remove(q, coll).getDeletedCount();
            return new long[]{1, del}; // (moved=1, removed=del(보통 1))
        }
        return new long[]{0, 0};
    }

    /** 다건 이동: 원본 → *_archive (moved, removed 반환) */
    private long[] moveMany(Long userNo, String coll, Instant now) {
        var q = userNoEq(userNo);
        List<Document> docs = mongo.find(q, Document.class, coll);
        long moved = 0;
        if (!docs.isEmpty()) {
            for (var d : docs) {
                d.put("_archivedAt", now);
                mongo.save(d, coll + "_archive"); // upsert
                moved++;
            }
            long del = mongo.remove(q, coll).getDeletedCount();
            return new long[]{moved, del};
        }
        return new long[]{0, 0};
    }

    /** 단건 복구: *_archive → 원본 (restored, removedFromArchive 반환) */
    private long[] restoreOne(Long userNo, String coll) {
        var q = idOrUserNoEq(userNo);
        Document doc = mongo.findOne(q, Document.class, coll + "_archive");
        if (doc != null) {
            doc.remove("_archivedAt");
            mongo.save(doc, coll);
            long del = mongo.remove(q, coll + "_archive").getDeletedCount();
            return new long[]{1, del};
        }
        return new long[]{0, 0};
    }

    /** 다건 복구: *_archive → 원본 (restored, removedFromArchive 반환) */
    private long[] restoreMany(Long userNo, String coll) {
        var q = userNoEq(userNo);
        List<Document> docs = mongo.find(q, Document.class, coll + "_archive");
        if (!docs.isEmpty()) {
            docs.forEach(d -> {
                d.remove("_archivedAt");
                mongo.save(d, coll);
            });
            long restored = docs.size();
            long del = mongo.remove(q, coll + "_archive").getDeletedCount();
            return new long[]{restored, del};
        }
        return new long[]{0, 0};
    }

    /** 일부 단건 컬렉션은 _id == userNo, 일부는 user_no 필드 사용 → OR 조건 */
    private Query idOrUserNoEq(Long userNo) {
        return new Query(new Criteria().orOperator(
                Criteria.where("_id").is(userNo),
                Criteria.where("user_no").is(userNo)
        ));
    }

    /** 다건 컬렉션 공통 쿼리 */
    private Query userNoEq(Long userNo) {
        return new Query(Criteria.where("user_no").is(userNo));
    }

    /** 점검/락 스위치(필요 시 환경변수/DB 플래그로 구현) */
    private boolean isDeleteGuardOn() {
        return false;
    }
}