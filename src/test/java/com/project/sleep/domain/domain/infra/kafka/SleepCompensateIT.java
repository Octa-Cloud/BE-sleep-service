package com.project.sleep.domain.domain.infra.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sleep.domain.domain.entity.ProcessedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {
                "sleep.user-delete.compensate.command",
                "sleep.user-delete.reply",
                "sleep.user-delete.compensate.command.dlt"
        }
)
@Testcontainers
class SleepCompensationIT {

    // ---- Kafka / Mongo beans ----
    @Autowired EmbeddedKafkaBroker embeddedKafka;
    @Autowired KafkaTemplate<String, String> kafkaTemplate;
    @Autowired MongoTemplate mongo;
    @Autowired ObjectMapper om;

    // 리스너 파티션 할당 대기용
    @Autowired KafkaListenerEndpointRegistry registry;

    // 임베디드 카프카가 넣어주는 bootstrap-servers
    @Value("${spring.embedded.kafka.brokers}")
    private String brokers;

    // ---- Testcontainers Mongo ----
    @Container
    static MongoDBContainer mongoC = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry r) {
        // Replica Set URI를 테스트에서만 강제
        r.add("spring.data.mongodb.uri", mongoC::getReplicaSetUrl);
    }

    private static final String COMP_CMD = "sleep.user-delete.compensate.command";
    private static final String REPLY    = "sleep.user-delete.reply";
    private static final String COMP_DLT = "sleep.user-delete.compensate.command.dlt";

    private Consumer<String, String> replyConsumer;
    private Consumer<String, String> dltConsumer;

    @BeforeEach
    void setUp() {
        // 리스너 컨테이너가 파티션 할당 받을 때까지 대기 (레이스컨디션 방지)
        registry.getListenerContainers().forEach(container ->
                ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic()));

        // DB 클린
        mongo.getDb().drop();

        // 테스트 컨슈머 준비
        replyConsumer = createConsumer("reply-group", REPLY);
        dltConsumer   = createConsumer("dlt-group", COMP_DLT);
    }

    @AfterEach
    void tearDown() {
        if (replyConsumer != null) replyConsumer.close();
        if (dltConsumer != null) dltConsumer.close();
    }

    @Test
    void compensate_success_restores_from_archive_and_sends_success_reply() throws Exception {
        long userNo = 766891164431155700L;
        String eventId = UUID.randomUUID().toString();

        seedArchiveDocs(userNo);

        String payload = om.createObjectNode().put("eventId", eventId).put("userNo", userNo).toString();
        kafkaTemplate.send(COMP_CMD, String.valueOf(userNo), payload).get(5, TimeUnit.SECONDS);

        var reply = pollOne(replyConsumer, Duration.ofSeconds(10));
        assertThat(reply).isNotNull();
        var n = om.readTree(reply.value());
        assertThat(n.path("status").asText()).isEqualTo("SUCCESS");
        assertThat(n.path("type").asText()).isEqualTo("COMPENSATE");
        assertThat(n.path("eventId").asText()).isEqualTo(eventId);
        assertThat(n.path("userNo").asLong()).isEqualTo(userNo);

        // 복구 확인
        assertThat(countById("sleep_goal", userNo)).isEqualTo(1);
        assertThat(countById("total_sleep_record", userNo)).isEqualTo(1);
        assertThat(countByUserNo("daily_sleep_record", userNo)).isEqualTo(2);
        assertThat(countByUserNo("daily_report", userNo)).isEqualTo(1);
        assertThat(countByUserNo("periodic_report", userNo)).isEqualTo(1);

        // 아카이브 비움 확인
        assertThat(countById("sleep_goal_archive", userNo)).isZero();
        assertThat(countById("total_sleep_record_archive", userNo)).isZero();
        assertThat(countByUserNo("daily_sleep_record_archive", userNo)).isZero();
        assertThat(countByUserNo("daily_report_archive", userNo)).isZero();
        assertThat(countByUserNo("periodic_report_archive", userNo)).isZero();

        // processed_event = SUCCESS
        var pe = mongo.findById(eventId, ProcessedEvent.class, "processed_event");
        assertThat(pe).isNotNull();
        assertThat(pe.getStatus()).isEqualTo("SUCCESS");
        assertThat(pe.getType()).isEqualTo("COMPENSATE");
    }

    @Test
    void compensate_fail_source_missing_sends_fail_reply_and_terminal_fail() throws Exception {
        long userNo = 1234L;
        String eventId = UUID.randomUUID().toString();

        String payload = om.createObjectNode()
                .put("eventId", eventId)
                .put("userNo", userNo)
                .toString();

        kafkaTemplate.send(COMP_CMD, String.valueOf(userNo), payload).get(5, TimeUnit.SECONDS);

        var reply = pollOne(replyConsumer, Duration.ofSeconds(10));
        assertThat(reply).isNotNull();
        var n = om.readTree(reply.value());
        assertThat(n.path("status").asText()).isEqualTo("FAIL");
        assertThat(n.path("type").asText()).isEqualTo("COMPENSATE");
        assertThat(n.path("code").asText()).isEqualTo("COMPENSATE_SOURCE_MISSING");

        // ✅ 비동기 처리 완료될 때까지 대기
        await().atMost(java.time.Duration.ofSeconds(5))
                .pollInterval(java.time.Duration.ofMillis(100))
                .untilAsserted(() -> {
                    var pe = mongo.findById(eventId, ProcessedEvent.class, "processed_event");
                    assertThat(pe).isNotNull();
                    assertThat(pe.getStatus()).isEqualTo("TERMINAL_FAIL");
                    assertThat(pe.getLastError()).contains("COMPENSATE_SOURCE_MISSING");
                });
    }
    @Test
    void toxic_payload_goes_to_dlt() throws Exception {
        String bad = "{\"eventId\":\"\",\"userNo\":0}";
        kafkaTemplate.send(COMP_CMD, "bad-key", bad).get(5, TimeUnit.SECONDS);

        assertThat(pollOne(replyConsumer, Duration.ofSeconds(3))).isNull();

        var dlt = pollOne(dltConsumer, Duration.ofSeconds(10));
        assertThat(dlt).isNotNull();
        assertThat(dlt.topic()).isEqualTo(COMP_DLT);
    }

    @Test
    void idempotent_redelivery_is_noop_on_second_try() throws Exception {
        long userNo = 9999L;
        String eventId = UUID.randomUUID().toString();

        seedArchiveDocs(userNo);

        String payload = om.createObjectNode().put("eventId", eventId).put("userNo", userNo).toString();

        kafkaTemplate.send(COMP_CMD, String.valueOf(userNo), payload).get(5, TimeUnit.SECONDS);
        var first = pollOne(replyConsumer, Duration.ofSeconds(10));
        assertThat(first).isNotNull();

        kafkaTemplate.send(COMP_CMD, String.valueOf(userNo), payload).get(5, TimeUnit.SECONDS);
        assertThat(pollOne(replyConsumer, Duration.ofSeconds(3))).isNull();

        var pe = mongo.findById(eventId, ProcessedEvent.class, "processed_event");
        assertThat(pe).isNotNull();
        assertThat(pe.getStatus()).isEqualTo("SUCCESS");
    }

    // ---- helpers ----
    private Consumer<String, String> createConsumer(String groupId, String topic) {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        var cf = new DefaultKafkaConsumerFactory<String, String>(props);
        var consumer = cf.createConsumer();
        consumer.subscribe(java.util.List.of(topic));   // embeddedKafka.consumeFrom... 필요 없음
        return consumer;
    }

    private ConsumerRecord<String, String> pollOne(Consumer<String, String> c, Duration timeout) {
        var records = c.poll(timeout);
        if (records.isEmpty()) return null;
        return records.iterator().next();
    }

    private void seedArchiveDocs(long userNo) {
        mongo.save(new Document("_id", userNo).append("_archivedAt", new Date())
                        .append("user_no", userNo).append("goal_sleep_time", 420),
                "sleep_goal_archive");

        mongo.save(new Document("_id", userNo).append("_archivedAt", new Date())
                        .append("user_no", userNo).append("total_sleep_minutes", 12345),
                "total_sleep_record_archive");

        mongo.save(new Document("_id", "dsr-"+userNo+"-1").append("_archivedAt", new Date())
                        .append("user_no", userNo).append("sleep_minutes", 300),
                "daily_sleep_record_archive");
        mongo.save(new Document("_id", "dsr-"+userNo+"-2").append("_archivedAt", new Date())
                        .append("user_no", userNo).append("sleep_minutes", 360),
                "daily_sleep_record_archive");

        mongo.save(new Document("_id", "dr-"+userNo+"-2025-10-07").append("_archivedAt", new Date())
                        .append("user_no", userNo).append("analysis_title", "테스트 리포트"),
                "daily_report_archive");

        mongo.save(new Document("_id", "pr-"+userNo+"-wk41").append("_archivedAt", new Date())
                        .append("user_no", userNo).append("period", "2025-W41"),
                "periodic_report_archive");
    }

    private long countById(String coll, long userNo) {
        return mongo.getDb().getCollection(coll)
                .countDocuments(new Document("_id", userNo));
    }

    private long countByUserNo(String coll, long userNo) {
        return mongo.getDb().getCollection(coll)
                .countDocuments(new Document("user_no", userNo));
    }
}