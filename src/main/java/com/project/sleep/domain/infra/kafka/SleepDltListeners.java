package com.project.sleep.domain.infra.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SleepDltListeners {

    private final DltLogger dlt;
    private final ObjectMapper om;              // ✔ 필요 (eventId 추출)
    private final ProcessedEventStore store;    // ✔ DLT에서 TERMINAL_FAIL 마킹만

    @KafkaListener(
            topics = "sleep.user-delete.command.dlt",
            groupId = "sleep-service-dlt",                 // ✔ 메인과 분리
            containerFactory = "kafkaManualAckFactory"     // ✔ 명시적 ack
    )
    public void onDeleteCmdDlt(
            ConsumerRecord<String,String> rec,
            Acknowledgment ack,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_TOPIC,     required = false) String oTopic,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_PARTITION, required = false) Integer oPart,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_OFFSET,    required = false) Long oOffset,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_FQCN,     required = false) String exClass,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_MESSAGE,  required = false) String exMsg
    ) {
        dlt.logAndCount("sleep.user-delete.command.dlt", rec, oTopic, oPart, oOffset, exClass, exMsg);

        try {
            JsonNode n = om.readTree(rec.value());
            String eventId = n.path("eventId").asText(null);
            if (eventId != null && !eventId.isBlank()) {
                store.markTerminalFailUpsert(eventId, "DELETE", "DELETE retries exhausted -> moved to DLT");
            }
        } catch (Exception ignore) {
            // 파싱 불가면 그냥 로그만 남기고 종료
        } finally {
            ack.acknowledge(); // ✔ reply 발행 금지
        }
    }

    @KafkaListener(
            topics = "sleep.user-delete.compensate.command.dlt",
            groupId = "sleep-service-dlt",
            containerFactory = "kafkaManualAckFactory"
    )
    public void onCompensateCmdDlt(
            ConsumerRecord<String,String> rec,
            Acknowledgment ack,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_TOPIC,     required = false) String oTopic,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_PARTITION, required = false) Integer oPart,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_OFFSET,    required = false) Long oOffset,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_FQCN,     required = false) String exClass,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_MESSAGE,  required = false) String exMsg
    ) {
        dlt.logAndCount("sleep.user-delete.compensate.command.dlt", rec, oTopic, oPart, oOffset, exClass, exMsg);

        try {
            JsonNode n = om.readTree(rec.value());
            String eventId = n.path("eventId").asText(null);
            if (eventId != null && !eventId.isBlank()) {
                store.markTerminalFailUpsert(eventId, "COMPENSATE", "COMPENSATE retries exhausted -> moved to DLT");
            }
        } catch (Exception ignore) {
        } finally {
            ack.acknowledge(); // ✔ reply 발행 금지
        }
    }
}