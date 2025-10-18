package com.project.sleep.domain.infra.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DltLogger {

    private final @Nullable MeterRegistry meter;

    public DltLogger(@Nullable MeterRegistry meter) {
        this.meter = meter; // 없을 수 있음 (테스트/로컬에서)
    }

    public void logAndCount(
            String dltTopic,
            ConsumerRecord<String, String> rec,
            String oTopic,
            Integer oPart,
            Long oOffset,
            String exClass,
            String exMsg
    ) {
        if (meter != null) {
            Counter.builder("kafka.dlt.count")
                    .tag("dltTopic", dltTopic)
                    .tag("origTopic", String.valueOf(oTopic == null ? "null" : oTopic))
                    .register(meter)
                    .increment();
        }

        log.error("[DLT] topic={}, origTopic={}, origPartition={}, origOffset={}, key={}, ts={}, exClass={}, exMsg={}, payload={}",
                dltTopic, oTopic, oPart, oOffset, rec.key(), rec.timestamp(), exClass, exMsg, rec.value());
    }
}