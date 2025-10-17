package com.project.sleep.domain.domain.infra.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.sleep.domain.domain.entity.ProcessedEvent;
import com.project.sleep.domain.domain.infra.kafka.support.KafkaMongoTestBase;
import com.project.sleep.domain.domain.service.SleepArchiveService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;

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
class RetryToDltBothIT extends KafkaMongoTestBase {

    private static final String DEL_CMD = "sleep.user-delete.command";
    private static final String REPLY   = "sleep.user-delete.reply";
    private static final String DLT     = "sleep.user-delete.command.dlt";

    @Autowired org.springframework.kafka.core.KafkaTemplate<String,String> kafka;

    // ✅ 실제 빈을 대체해 중복 제거
    @MockBean SleepArchiveService archiveSpy;

    // ✅ 임베디드 카프카 주소를 강제로 바인딩
    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry r) {
        r.add("spring.kafka.bootstrap-servers",
                () -> System.getProperty("spring.embedded.kafka.brokers"));
    }

    @Test
    void technical_exception_retries_then_dlt_and_terminal_fail() throws Exception {
        long userNo = 424242L;
        String eventId = UUID.randomUUID().toString();

        doThrow(new RuntimeException("boom"))
                .when(archiveSpy).archiveAndDeleteAllOfUser(userNo);

        var dltC   = newConsumerOnTopic(DLT);
        var replyC = newConsumerOnTopic(REPLY);

        String payload = om.createObjectNode()
                .put("eventId", eventId).put("userNo", userNo).toString();
        kafka.send(DEL_CMD, String.valueOf(userNo), payload).get(5, TimeUnit.SECONDS);

        // DLT 도달
        List<ConsumerRecord<String,String>> dlt =
                pollUntil(dltC, 1, Duration.ofSeconds(30));
        assertThat(dlt).hasSize(1);

        // DLT 리스너가 FAIL reply 발행
        List<ConsumerRecord<String,String>> replies =
                pollUntil(replyC, 1, Duration.ofSeconds(30));
        assertThat(replies).hasSize(1);

        JsonNode n = om.readTree(replies.get(0).value());
        assertThat(n.path("status").asText()).isEqualTo("FAIL");
        assertThat(n.path("type").asText()).isEqualTo("DELETE");
        assertThat(n.path("code").asText()).isEqualTo("RETRIES_EXHAUSTED");

        var pe = mongo.findById(eventId, ProcessedEvent.class, "processed_event");
        assertThat(pe).isNotNull();
        assertThat(pe.getStatus()).isEqualTo("TERMINAL_FAIL");

        reset(archiveSpy);
    }
}