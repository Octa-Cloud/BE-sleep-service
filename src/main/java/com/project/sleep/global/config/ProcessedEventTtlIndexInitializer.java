package com.project.sleep.global.config;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;


@Component
@RequiredArgsConstructor
public class ProcessedEventTtlIndexInitializer {

    private final MongoTemplate mongo;

    @Value("${sleep.processed-event.ttl-seconds:2592000}") // 기본 30일
    private long ttlSeconds;

    @PostConstruct
    public void ensureTtlIndex() {
        var coll = mongo.getDb().getCollection("processed_event");
        boolean exists = StreamSupport.stream(coll.listIndexes().spliterator(), false)
                .anyMatch(ix -> "updatedAt_1".equals(String.valueOf(ix.get("name"))));
        if (!exists) {
            coll.createIndex(
                    Indexes.ascending("updatedAt"),
                    new IndexOptions().expireAfter(ttlSeconds, TimeUnit.SECONDS)
            );
        }
    }
}
