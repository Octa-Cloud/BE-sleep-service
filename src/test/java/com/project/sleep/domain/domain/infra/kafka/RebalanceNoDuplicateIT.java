// src/test/java/com/project/sleep/domain/domain/infra/kafka/RebalanceNoDuplicateIT.java
package com.project.sleep.domain.domain.infra.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.sleep.domain.domain.entity.ProcessedEvent;
import com.project.sleep.domain.domain.infra.kafka.support.KafkaMongoTestBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
@org.springframework.test.context.ActiveProfiles("test")
class RebalanceNoDuplicateIT extends KafkaMongoTestBase {

    private static final String DEL_CMD = "sleep.user-delete.command";
    private static final String REPLY   = "sleep.user-delete.reply";

    @Autowired org.springframework.kafka.core.KafkaTemplate<String,String> kafka;

    @Test
    void no_duplicate_processing_during_rebalance() throws Exception {
        // reply 컨슈머 미리 준비
        Consumer<String,String> replyC = newConsumerOnTopic(REPLY);

        int N = 30;
        List<String> eventIds = new ArrayList<>(N);

        // 1) N건 발행
        for (int i = 0; i < N; i++) {
            long userNo = 100000L + i;
            String eventId = UUID.randomUUID().toString();
            eventIds.add(eventId);
            String payload = om.createObjectNode().put("eventId", eventId).put("userNo", userNo).toString();
            kafka.send(DEL_CMD, String.valueOf(userNo), payload).get(5, TimeUnit.SECONDS);
        }

        // 2) 리밸런스 유도: 같은 그룹으로 "가짜 컨슈머"를 붙였다가 곧바로 닫는다
        Consumer<String,String> rebalancer = newConsumerOnTopic(DEL_CMD); // 같은 토픽에 같은 그룹으로
        rebalancer.close(); // 즉시 닫아서 빠른 리밸런스 유발

        // 3) reply를 충분히 수집
        List<ConsumerRecord<String,String>> replies = pollUntil(replyC, N, Duration.ofSeconds(20));
        assertThat(replies).hasSize(N);

        // 4) 각 이벤트는 SUCCESS 또는 FAIL 중 1회만 응답
        Map<String, Long> byEvent =
                replies.stream()
                        .map(r -> {
                            try { return om.readTree(r.value()); } catch (Exception e) { throw new RuntimeException(e); }
                        })
                        .collect(Collectors.groupingBy(n -> n.path("eventId").asText(), Collectors.counting()));

        assertThat(byEvent.values()).allMatch(cnt -> cnt == 1L);

        // 5) processed_event도 중복 없이 N건만 존재
        List<ProcessedEvent> stored = eventIds.stream()
                .map(id -> mongo.findById(id, ProcessedEvent.class, "processed_event"))
                .filter(Objects::nonNull)
                .toList();
        assertThat(stored).hasSize(N);
    }
}