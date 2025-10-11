// com.project.sleep.domain.infra.kafka.UserDeletionConsumers.java
package com.project.sleep.domain.infra.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sleep.domain.domain.service.SleepArchiveService;
import com.project.sleep.domain.domain.entity.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;

/**
 * 삭제/보상 컨슈머.
 * 공통 정책
 * - 멱등: processed_event(eventId=PK)로 상태 추적 → 성공 건 재처리 스킵.
 * - 독성 페이로드: InvalidPayloadException 던져서 즉시 DLT(exclude).
 * - 비즈니스는 Mongo 트랜잭션(TransactionTemplate)으로 래핑.
 * - reply는 동기 전송(.get())으로 브로커 수신 보장.
 * - 성공 후 수동 ACK(오프셋 커밋).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionConsumers {

    private final MongoTemplate mongo;
    private final MongoTransactionManager txManager;
    private final SleepArchiveService archive;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper om;

    // --- 멱등 시작 마킹 ---
    private boolean tryBegin(String eventId, String type) {
        try {
            mongo.insert(ProcessedEvent.builder()
                    .eventId(eventId).type(type).status("IN_PROGRESS")
                    .attempts(1).updatedAt(Instant.now()).build());
            return true; // 최초 처리
        } catch (DuplicateKeyException e) {
            var existing = mongo.findById(eventId, ProcessedEvent.class);
            return existing == null || !"SUCCESS".equals(existing.getStatus()); // 성공이면 스킵
        }
    }

    private void markSuccess(String eventId) {
        var pe = mongo.findById(eventId, ProcessedEvent.class);
        if (pe != null) {
            pe.setStatus("SUCCESS");
            pe.setUpdatedAt(Instant.now());
            mongo.save(pe);
        }
    }

    private void markError(String eventId, String msg) {
        var pe = mongo.findById(eventId, ProcessedEvent.class);
        if (pe != null) {
            pe.setStatus("ERROR");
            pe.setAttempts(pe.getAttempts() + 1);
            pe.setLastError(msg);
            pe.setUpdatedAt(Instant.now());
            mongo.save(pe);
        }
    }

    // === 삭제 명령 처리 ===
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "true",
            dltTopicSuffix = ".dlt",
            exclude = {InvalidPayloadException.class} // 독성은 즉시 DLT
    )
    @KafkaListener(
            topics = "user.delete.command",
            groupId = "sleep-service",
            containerFactory = "kafkaManualAckFactory"
    )
    public void onDeleteCommand(ConsumerRecord<String, String> rec, Acknowledgment ack) throws Exception {
        // 0) 공통 파싱/검증 가드
        final Parsed p;
        try {
            p = validateAndParse(rec.value());
        } catch (InvalidPayloadException bad) {
            // ⬇️ 독성 페이로드 로깅(즉시 DLT)
            log.error("[sleep] toxic payload -> DLT, value={}", rec.value());
            throw bad;
        }

        String eventId = p.eventId();
        long userNo = p.userNo();

        // 멱등: 이미 성공된 이벤트면 ACK 후 종료
        if (!tryBegin(eventId, "DELETE")) {
            ack.acknowledge();
            return;
        }

        var tmpl = new TransactionTemplate(txManager);
        try {
            // 1) Mongo TX 내 비즈니스
            tmpl.execute(status -> { archive.archiveAndDeleteAllOfUser(userNo); return null; });

            // 2) 성공 reply 동기 전송
            var reply = om.createObjectNode()
                    .put("eventId", eventId).put("userNo", userNo)
                    .put("status", "SUCCESS").put("type", "DELETE");
            kafka.send("user.delete.reply", String.valueOf(userNo), om.writeValueAsString(reply)).get();

            // 3) 상태 마킹 + ACK
            markSuccess(eventId);
            ack.acknowledge();

            log.info("[sleep] DELETE ok key={}, eventId={}, userNo={}", rec.key(), eventId, userNo);
        } catch (Exception ex) {
            markError(eventId, ex.getMessage());
            throw ex; // 재시도 → 실패 시 .dlt
        }
    }

    // === 보상(복구) 처리 ===
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "true",
            dltTopicSuffix = ".dlt",
            exclude = {InvalidPayloadException.class}
    )
    @KafkaListener(
            topics = "user.delete.compensate",
            groupId = "sleep-service",
            containerFactory = "kafkaManualAckFactory"
    )
    public void onCompensate(ConsumerRecord<String, String> rec, Acknowledgment ack) throws Exception {
        final Parsed p;
        try {
            p = validateAndParse(rec.value());
        } catch (InvalidPayloadException bad) {
            log.error("[sleep] toxic payload -> DLT, value={}", rec.value());
            throw bad;
        }

        String eventId = p.eventId();
        long userNo = p.userNo();

        if (!tryBegin(eventId, "COMPENSATE")) {
            ack.acknowledge();
            return;
        }

        var tmpl = new TransactionTemplate(txManager);
        try {
            tmpl.execute(status -> { archive.restoreAllOfUser(userNo); return null; });

            var reply = om.createObjectNode()
                    .put("eventId", eventId).put("userNo", userNo)
                    .put("status", "SUCCESS").put("type", "COMPENSATE");
            kafka.send("user.delete.reply", String.valueOf(userNo), om.writeValueAsString(reply)).get();

            markSuccess(eventId);
            ack.acknowledge();

            log.info("[sleep] COMPENSATE ok key={}, eventId={}, userNo={}", rec.key(), eventId, userNo);
        } catch (Exception ex) {
            log.warn("COMPENSATE failed userNo={}, eventId={}, err={}", userNo, eventId, ex.toString());
            markError(eventId, ex.getMessage());
            throw ex;
        }
    }

    // === 공통 파싱/검증 ===
    private record Parsed(String eventId, long userNo) {}

    /**
     * 독성 페이로드 판별:
     * - 필수 필드 누락/형식 오류/음수 등은 InvalidPayloadException → 즉시 DLT.
     * - JSON 파싱 실패 또한 동일 정책 적용.
     */
    private Parsed validateAndParse(String raw) {
        try {
            JsonNode n = om.readTree(raw);
            if (n == null || !n.hasNonNull("eventId") || !n.hasNonNull("userNo")) {
                throw new InvalidPayloadException("Missing required fields: eventId/userNo");
            }
            String eventId = n.get("eventId").asText("").trim();
            if (eventId.isEmpty()) throw new InvalidPayloadException("eventId is empty");
            if (!n.get("userNo").canConvertToLong()) throw new InvalidPayloadException("userNo is not a long");
            long userNo = n.get("userNo").asLong();
            if (userNo <= 0) throw new InvalidPayloadException("userNo <= 0");
            return new Parsed(eventId, userNo);
        } catch (InvalidPayloadException ipe) {
            throw ipe; // 독성 → 즉시 DLT
        } catch (Exception ex) {
            throw new InvalidPayloadException("Invalid JSON: " + ex.getMessage(), ex);
        }
    }
}