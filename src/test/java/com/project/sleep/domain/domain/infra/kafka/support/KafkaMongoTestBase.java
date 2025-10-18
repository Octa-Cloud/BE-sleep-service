// src/test/java/.../support/KafkaMongoTestBase.java
package com.project.sleep.domain.domain.infra.kafka.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;

import static org.springframework.kafka.test.utils.ContainerTestUtils.waitForAssignment;

public abstract class KafkaMongoTestBase {

    // --- Mongo(Testcontainers) : 추상 클래스에서는 @Container 대신 수동 start ---
    protected static final MongoDBContainer MONGO =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    static {
        MONGO.start(); // ★ 반드시 먼저 시작시켜 놓기
    }

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry r) {
        r.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
        r.add("spring.kafka.properties.security.protocol", () -> "PLAINTEXT");
        r.add("spring.kafka.admin.auto-create", () -> "true");
        r.add("spring.kafka.admin.fail-fast", () -> "false");
        r.add("spring.kafka.consumer.properties.security.protocol", () -> "PLAINTEXT");
        r.add("spring.kafka.producer.properties.security.protocol", () -> "PLAINTEXT");
    }

    // required=false 로 NPE 방지 (컨텍스트 로딩 실패 시 @BeforeEach에서 건너뛰게)
    @Autowired(required = false) protected EmbeddedKafkaBroker embeddedKafka;
    @Autowired(required = false) protected KafkaListenerEndpointRegistry registry;
    @Autowired(required = false) protected MongoTemplate mongo;
    @Autowired protected ObjectMapper om;

    @Value("${spring.embedded.kafka.brokers:localhost:9092}")
    protected String brokers;

    protected final List<org.apache.kafka.clients.consumer.Consumer<String,String>> closeLater = new ArrayList<>();

    @BeforeEach
    void waitContainersReady() {
        if (registry != null && embeddedKafka != null) {
            registry.getListenerContainers()
                    .forEach(c -> waitForAssignment(c, embeddedKafka.getPartitionsPerTopic()));
        }
    }

    @AfterEach
    void closeConsumers() {
        closeLater.forEach(c -> {
            try { c.close(); } catch (Exception ignore) {}
        });
        closeLater.clear();
        if (mongo != null) {
            // 테스트 데이터 격리
            mongo.getDb().drop();
        }
    }

    protected org.apache.kafka.clients.consumer.Consumer<String,String>
    newConsumerOnTopic(String topic) {
        var props = new HashMap<String,Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "g-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        var c = new org.springframework.kafka.core.DefaultKafkaConsumerFactory<String,String>(props)
                .createConsumer();
        c.subscribe(List.of(topic));
        // 파티션 바인딩 유도 & 처음부터 읽기
        c.poll(Duration.ofMillis(200));
        if (!c.assignment().isEmpty()) c.seekToBeginning(c.assignment());
        closeLater.add(c);
        return c;
    }

    protected <K,V> List<org.apache.kafka.clients.consumer.ConsumerRecord<K,V>>
    pollUntil(org.apache.kafka.clients.consumer.Consumer<K,V> c, int expected, Duration max) {
        long end = System.currentTimeMillis() + max.toMillis();
        List<org.apache.kafka.clients.consumer.ConsumerRecord<K,V>> all = new ArrayList<>();
        while (System.currentTimeMillis() < end && all.size() < expected) {
            var polled = c.poll(Duration.ofMillis(200));
            polled.forEach(all::add);
        }
        return all;
    }

    protected void seedOriginal(long userNo) {
        if (mongo == null) return;
        mongo.save(new Document("_id", userNo).append("user_no", userNo), "sleep_goal");
        mongo.save(new Document("_id", userNo).append("user_no", userNo), "total_sleep_record");
    }

}