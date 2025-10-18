// src/test/java/com/project/sleep/domain/domain/infra/kafka/RetryToDltHeadersIT.java
package com.project.sleep.domain.domain.infra.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.sleep.domain.domain.infra.kafka.support.KafkaMongoTestBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

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
class RetryToDltHeadersIT extends KafkaMongoTestBase {

    private static final String DEL_CMD = "sleep.user-delete.command";
    private static final String REPLY   = "sleep.user-delete.reply";
    private static final String DLT     = "sleep.user-delete.command.dlt";

    @Autowired org.springframework.kafka.core.KafkaTemplate<String,String> kafka;

    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry r) {
        r.add("spring.kafka.bootstrap-servers",
                () -> System.getProperty("spring.embedded.kafka.brokers"));
    }

    @Test
    void invalid_payload_goes_direct_to_dlt_with_headers_and_fail_reply() throws Exception {
        var dltC   = newConsumerOnTopic(DLT);
        var replyC = newConsumerOnTopic(REPLY);

        kafka.send(DEL_CMD, "key-x", "{\"foo\":1}").get(5, TimeUnit.SECONDS);

        var dlt = pollUntil(dltC, 1, Duration.ofSeconds(30));   // ⬅ 30s
        assertThat(dlt).hasSize(1);

        var h = dlt.get(0).headers();
        assertThat(h.lastHeader("kafka_dlt-exception-fqcn")).isNotNull();
        assertThat(new String(h.lastHeader("kafka_dlt-exception-fqcn").value()))
                .contains("InvalidPayloadException");

        var replies = pollUntil(replyC, 1, Duration.ofSeconds(30)); // ⬅ 30s
        assertThat(replies).hasSize(1);

        var n = om.readTree(replies.get(0).value());
        assertThat(n.path("status").asText()).isEqualTo("FAIL");
        assertThat(n.path("type").asText()).isEqualTo("DELETE");
        assertThat(n.path("code").asText()).isEqualTo("RETRIES_EXHAUSTED");
    }
}