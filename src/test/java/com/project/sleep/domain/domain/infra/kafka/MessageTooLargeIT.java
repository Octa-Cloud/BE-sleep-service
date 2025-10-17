// src/test/java/com/project/sleep/domain/domain/infra/kafka/MessageTooLargeIT.java
package com.project.sleep.domain.domain.infra.kafka;

import com.project.sleep.domain.domain.infra.kafka.support.KafkaMongoTestBase;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@org.springframework.kafka.test.context.EmbeddedKafka(
        partitions = 3,
        topics = {
                "sleep.user-delete.command",
                "sleep.user-delete.compensate.command",
                "sleep.user-delete.reply",
                "sleep.user-delete.command.dlt",
                "sleep.user-delete.compensate.command.dlt"
        }
)
@org.springframework.test.context.ActiveProfiles("test")class MessageTooLargeIT extends KafkaMongoTestBase {

    private static final String TOPIC = "sleep.user-delete.command";

    @Test
    void producer_rejects_when_message_exceeds_max_request_size() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1024); // 1KB

        try (Producer<String,String> prod = new KafkaProducer<>(p)) {
            String tooLarge = "x".repeat(10_000); // 10KB
            var rec = new ProducerRecord<>(TOPIC, "k", tooLarge);
            assertThatThrownBy(() -> prod.send(rec).get())
                    .hasMessageContaining("RecordTooLargeException");
        }
    }
}