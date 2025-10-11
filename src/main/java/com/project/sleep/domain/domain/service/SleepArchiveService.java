// com.project.sleep.domain.domain.service.SleepArchiveService.java
package com.project.sleep.domain.domain.service;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * 유저별 수면 데이터의 "아카이브 → 삭제" 및 "복구" 서비스.
 *
 * 설계 요점
 * - 아카이브 컬렉션(*_archive)은 TTL 인덱스로 수명 관리(별도 초기화 컴포넌트에서 보증).
 * - insert 대신 save 사용: _id가 유지되면 upsert처럼 동작 → 충돌/중복 방지.
 * - 단건/다건 컬렉션을 분리 처리하여 쿼리/IO 효율 유지.
 */
@Service
@RequiredArgsConstructor
public class SleepArchiveService {

    private final MongoTemplate mongo;

    // === 외부 API (Mongo 트랜잭션 컨텍스트 안에서 호출) ===
    public void archiveAndDeleteAllOfUser(Long userNo) {
        Instant now = Instant.now();

        // 1) 단일 도큐먼트 계열
        moveOne(userNo, "sleep_goal", now);
        moveOne(userNo, "total_sleep_record", now);

        // 2) 다건 도큐먼트 계열
        moveMany(userNo, "daily_report", now);
        moveMany(userNo, "daily_sleep_record", now);
        moveMany(userNo, "periodic_report", now);
    }

    public void restoreAllOfUser(Long userNo) {
        // 1) 단일 도큐먼트 계열
        restoreOne(userNo, "sleep_goal");
        restoreOne(userNo, "total_sleep_record");

        // 2) 다건 도큐먼트 계열
        restoreMany(userNo, "daily_report");
        restoreMany(userNo, "daily_sleep_record");
        restoreMany(userNo, "periodic_report");
    }

    // === 내부 헬퍼 ===
    private Query idOrUserNoEq(Long userNo) {
        return new Query(new Criteria().orOperator(
                Criteria.where("_id").is(userNo),    // 일부 단건 컬렉션은 _id == userNo
                Criteria.where("user_no").is(userNo) // 일부 컬렉션은 user_no 필드로 매핑
        ));
    }

    private Query userNoEq(Long userNo) {
        return new Query(Criteria.where("user_no").is(userNo));
    }

    // 단건 이동(원본 → *_archive)
    private void moveOne(Long userNo, String coll, Instant now) {
        var q = idOrUserNoEq(userNo);
        Document doc = mongo.findOne(q, Document.class, coll);
        if (doc != null) {
            doc.put("_archivedAt", now);
            // save: _id 유지 시 upsert처럼 동작(아카이브 중복 방지)
            mongo.save(doc, coll + "_archive");
            mongo.remove(q, coll);
        }
    }

    // 다건 이동(원본 → *_archive)
    private void moveMany(Long userNo, String coll, Instant now) {
        var q = userNoEq(userNo);
        List<Document> docs = mongo.find(q, Document.class, coll);
        if (!docs.isEmpty()) {
            for (var d : docs) {
                d.put("_archivedAt", now);
                mongo.save(d, coll + "_archive"); // _id 존재 시 upsert
            }
            mongo.remove(q, coll);
        }
    }

    // 단건 복구(*_archive → 원본)
    private void restoreOne(Long userNo, String coll) {
        var q = idOrUserNoEq(userNo);
        Document doc = mongo.findOne(q, Document.class, coll + "_archive");
        if (doc != null) {
            doc.remove("_archivedAt");
            mongo.save(doc, coll);            // 원본에 upsert
            mongo.remove(q, coll + "_archive");
        }
    }

    // 다건 복구(*_archive → 원본)
    private void restoreMany(Long userNo, String coll) {
        var q = userNoEq(userNo);
        var docs = mongo.find(q, Document.class, coll + "_archive");
        if (!docs.isEmpty()) {
            docs.forEach(d -> {
                d.remove("_archivedAt");
                mongo.save(d, coll);          // upsert
            });
            mongo.remove(q, coll + "_archive");
        }
    }
}