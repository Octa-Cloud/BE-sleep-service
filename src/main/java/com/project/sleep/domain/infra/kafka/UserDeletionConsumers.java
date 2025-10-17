package com.project.sleep.domain.infra.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sleep.domain.domain.service.SleepArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionConsumers {
    /**
     * Kafka 기반 사용자 삭제/보상 명령 컨슈머.
     *
     * 기능
     * - 토픽:
     *   - sleep.user-delete.command → 사용자 데이터 삭제
     *   - sleep.user-delete.compensate.command → 삭제 보상(복구)
     * - MongoDB 트랜잭션 내에서 수면 데이터 아카이브/복구 로직 실행.
     * - Kafka Reply 토픽(sleep.user-delete.reply)으로 SUCCESS / FAIL 결과 전송.
     *
     * 예외 처리 정책
     * - InvalidPayloadException: 독성 페이로드 → 즉시 DLT.
     * - BusinessRuleException: 비즈니스 규칙 위반 → FAIL reply + TERMINAL_FAIL 마킹 + ACK.
     * - 그 외(Exception): 기술적 장애 → 재시도/DTL 체인으로 위임.
     *
     * 내부 정책
     * - 멱등성: processed_event(eventId=PK) 기반 상태 추적.
     * - 상태 마킹: ProcessedEventStore를 통해 IN_PROGRESS / SUCCESS / ERROR / TERMINAL_FAIL 관리.
     * - 트랜잭션: MongoTransactionManager + TransactionTemplate으로 Mongo 작업 원자성 확보.
     * - Reply 전송: ResilientSender.sendSync()로 브로커 수신 보장(acks=all).
     */
    // processed_event 관리는 공통 스토어로 위임
    private final ProcessedEventStore store;

    private final MongoTransactionManager txManager;
    private final SleepArchiveService archive;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper om;
    private final ResilientSender sender;

    // === 삭제 명령 처리 ===
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            dltTopicSuffix = ".dlt",
            exclude = {InvalidPayloadException.class}
    )
    @KafkaListener(
            topics = "sleep.user-delete.command",
            groupId = "sleep-service",
            containerFactory = "kafkaManualAckFactory"
    )
    public void onDeleteCommand(ConsumerRecord<String, String> rec, Acknowledgment ack) throws Exception {
        final Parsed p = parseOrThrow(rec.value());

        String eventId = p.eventId();
        long userNo = p.userNo();

        if (!store.tryBegin(eventId, "DELETE")) { // 멱등
            ack.acknowledge();
            return;
        }

        var tmpl = new TransactionTemplate(txManager);
        try {
            tmpl.execute(status -> { archive.archiveAndDeleteAllOfUser(userNo); return null; });

            var reply = om.createObjectNode()
                    .put("eventId", eventId).put("userNo", userNo)
                    .put("status", "SUCCESS").put("type", "DELETE");
            sender.sendSync("sleep.user-delete.reply", String.valueOf(userNo), om.writeValueAsString(reply));

            store.markSuccess(eventId);
            ack.acknowledge();

        } catch (BusinessRuleException brx) {
            var fail = om.createObjectNode()
                    .put("eventId", eventId).put("userNo", userNo)
                    .put("status", "FAIL").put("type", "DELETE")
                    .put("code", brx.getCode().name())
                    .put("message", brx.getMessage());
            sender.sendSync("sleep.user-delete.reply", String.valueOf(userNo), om.writeValueAsString(fail));

            store.markTerminalFailUpsert(eventId, "DELETE", brx.getCode() + ": " + brx.getMessage());
            ack.acknowledge();
            log.warn("[sleep] DELETE business-fail code={}, userNo={}, eventId={}, msg={}",
                    brx.getCode(), userNo, eventId, brx.getMessage());

        } catch (Exception ex) {
            store.markError(eventId, ex.getMessage()); // 기술장애 → 재시도/DTL
            throw ex;
        }
    }

    // === 보상(복구) 처리 ===
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            dltTopicSuffix = ".dlt",
            exclude = {InvalidPayloadException.class}
    )
    @KafkaListener(
            topics = "sleep.user-delete.compensate.command",
            groupId = "sleep-service",
            containerFactory = "kafkaManualAckFactory"
    )
    public void onCompensate(ConsumerRecord<String, String> rec, Acknowledgment ack) throws Exception {
        final Parsed p = parseOrThrow(rec.value());

        String eventId = p.eventId();
        long userNo = p.userNo();

        if (!store.tryBegin(eventId, "COMPENSATE")) {
            ack.acknowledge();
            return;
        }

        var tmpl = new TransactionTemplate(txManager);
        try {
            tmpl.execute(status -> { archive.restoreAllOfUser(userNo); return null; });

            var ok = om.createObjectNode()
                    .put("eventId", eventId)
                    .put("userNo", userNo)
                    .put("status", "SUCCESS")
                    .put("type", "COMPENSATE");
            sender.sendSync("sleep.user-delete.reply", String.valueOf(userNo), om.writeValueAsString(ok));

            store.markSuccess(eventId);
            ack.acknowledge();

            log.info("[sleep] COMPENSATE ok key={}, eventId={}, userNo={}", rec.key(), eventId, userNo);

        } catch (BusinessRuleException brx) {
            var fail = om.createObjectNode()
                    .put("eventId", eventId)
                    .put("userNo", userNo)
                    .put("status", "FAIL")
                    .put("type", "COMPENSATE")
                    .put("code", brx.getCode().name())
                    .put("message", brx.getMessage());
            sender.sendSync("sleep.user-delete.reply", String.valueOf(userNo), om.writeValueAsString(fail));

            store.markTerminalFailUpsert(eventId, "COMPENSATE", brx.getCode() + ": " + brx.getMessage());
            ack.acknowledge();

            log.warn("[sleep] COMPENSATE business-fail code={}, userNo={}, eventId={}, msg={}",
                    brx.getCode(), userNo, eventId, brx.getMessage());

        } catch (Exception ex) {
            store.markError(eventId, ex.getMessage());
            throw ex;
        }
    }

    // === 공통 파싱/검증 ===
    private record Parsed(String eventId, long userNo) {}

    private Parsed parseOrThrow(String raw) {
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