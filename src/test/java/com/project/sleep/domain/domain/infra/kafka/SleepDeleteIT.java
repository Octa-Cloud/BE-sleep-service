// src/test/java/com/project/sleep/domain/domain/infra/kafka/SleepDeleteIT.java
package com.project.sleep.domain.domain.infra.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.sleep.domain.domain.entity.ProcessedEvent;
import com.project.sleep.domain.domain.infra.kafka.support.KafkaMongoTestBase;
import com.project.sleep.domain.domain.service.SleepArchiveService;
import com.project.sleep.domain.infra.kafka.BusinessRuleException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EmbeddedKafka(
        partitions = 3,
        topics = {
                "sleep.user-delete.command",
                "sleep.user-delete.compensate.command",
                "sleep.user-delete.reply",
                "sleep.user-delete.command.dlt",
                "sleep.user-delete.compensate.command.dlt"
        }
)
@ActiveProfiles("test")
class SleepDeleteIT extends KafkaMongoTestBase {

    private static final String DEL_CMD = "sleep.user-delete.command";
    private static final String REPLY   = "sleep.user-delete.reply";

    @Autowired org.springframework.kafka.core.KafkaTemplate<String,String> kafka;

    // ✅ 실제 빈을 대체
    @org.springframework.boot.test.mock.mockito.MockBean
    com.project.sleep.domain.domain.service.SleepArchiveService archiveSpy;

    @DynamicPropertySource
    static void kafkaProps(org.springframework.test.context.DynamicPropertyRegistry r) {
        r.add("spring.kafka.bootstrap-servers",
                () -> System.getProperty("spring.embedded.kafka.brokers"));
    }

    private org.apache.kafka.clients.consumer.Consumer<String,String> replyConsumer;

    @org.junit.jupiter.api.BeforeEach
    void setUp() { replyConsumer = newConsumerOnTopic(REPLY); }

    @org.junit.jupiter.api.Test
    void delete_success_flow() throws Exception {
        long userNo = 777777L;
        var eventId = java.util.UUID.randomUUID().toString();
        seedOriginal(userNo);

        String payload = om.createObjectNode().put("eventId", eventId).put("userNo", userNo).toString();
        kafka.send(DEL_CMD, String.valueOf(userNo), payload).get(5, java.util.concurrent.TimeUnit.SECONDS);

        var replies = pollUntil(replyConsumer, 1, java.time.Duration.ofSeconds(30)); // ⬅ 30s
        org.assertj.core.api.Assertions.assertThat(replies).hasSize(1);

        var n = om.readTree(replies.get(0).value());
        org.assertj.core.api.Assertions.assertThat(n.path("status").asText()).isEqualTo("SUCCESS");
        org.assertj.core.api.Assertions.assertThat(n.path("type").asText()).isEqualTo("DELETE");

        var pe = mongo.findById(eventId, com.project.sleep.domain.domain.entity.ProcessedEvent.class, "processed_event");
        org.assertj.core.api.Assertions.assertThat(pe).isNotNull();
        org.assertj.core.api.Assertions.assertThat(pe.getStatus()).isEqualTo("SUCCESS");
    }

    @org.junit.jupiter.api.Test
    void delete_business_fail_flow() throws Exception {
        long userNo = 888888L;
        var eventId = java.util.UUID.randomUUID().toString();

        org.mockito.Mockito.doThrow(new com.project.sleep.domain.infra.kafka.BusinessRuleException(
                        com.project.sleep.domain.domain.entity.FailureCode.CONSISTENCY_MISMATCH, "moved!=removed"))
                .when(archiveSpy).archiveAndDeleteAllOfUser(userNo);

        String payload = om.createObjectNode().put("eventId", eventId).put("userNo", userNo).toString();
        kafka.send(DEL_CMD, String.valueOf(userNo), payload).get(5, java.util.concurrent.TimeUnit.SECONDS);

        var replies = pollUntil(replyConsumer, 1, java.time.Duration.ofSeconds(30)); // ⬅ 30s
        org.assertj.core.api.Assertions.assertThat(replies).hasSize(1);

        var n = om.readTree(replies.get(0).value());
        org.assertj.core.api.Assertions.assertThat(n.path("status").asText()).isEqualTo("FAIL");
        org.assertj.core.api.Assertions.assertThat(n.path("type").asText()).isEqualTo("DELETE");
        org.assertj.core.api.Assertions.assertThat(n.path("code").asText()).isEqualTo("CONSISTENCY_MISMATCH");

        var pe = mongo.findById(eventId, com.project.sleep.domain.domain.entity.ProcessedEvent.class, "processed_event");
        org.assertj.core.api.Assertions.assertThat(pe).isNotNull();
        org.assertj.core.api.Assertions.assertThat(pe.getStatus()).isEqualTo("TERMINAL_FAIL");
    }
}