// com.project.sleep.global.config.KafkaConfig.java
package com.project.sleep.global.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * - @EnableKafkaRetryTopic: @RetryableTopic 재시도/DTL 기능 활성화.
 * - 수동 ACK(MANUAL_IMMEDIATE), auto commit OFF → 처리/저장 성공 후에만 오프셋 커밋.
 * - max.poll.records / max.poll.interval.ms 는 비즈니스 처리시간에 맞게 조정.
 */
@Configuration
@EnableKafka
@EnableKafkaRetryTopic
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties());
        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);      // 수동 커밋
        cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // 개발/테스트에 유리
        cfg.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);           // 처리시간 고려한 배치 크기
        cfg.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600_000);  // 10분(아카이브/복구 여유)
        return new DefaultKafkaConsumerFactory<>(cfg);
    }

    @Bean(name = "kafkaManualAckFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaManualAckFactory(
            ConsumerFactory<String, String> cf) {
        var f = new ConcurrentKafkaListenerContainerFactory<String, String>();
        f.setConsumerFactory(cf);
        f.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        f.setConcurrency(1); // 파티션 1개 기준. 증가 시 파티션 수와 함께 조정
        return f;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory(KafkaProperties props) {
        Map<String, Object> cfg = new HashMap<>(props.buildProducerProperties());
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.ACKS_CONFIG, "all");
        cfg.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        cfg.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // 순서 보장
        return new DefaultKafkaProducerFactory<>(cfg);
    }

    /**
     * @RetryableTopic 의 지연 재시도(1s→2s→4s...) 스케줄링에 쓰는 타이머 스레드풀.
     */
    @Bean
    public org.springframework.scheduling.TaskScheduler taskScheduler() {
        var t = new org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler();
        t.setPoolSize(2);
        t.setThreadNamePrefix("sleep-sched-");
        t.initialize();
        return t;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
        return new KafkaTemplate<>(pf);
    }
}