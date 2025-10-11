// src/main/java/com/project/sleep/global/config/ArchiveTtlIndexInitializer.java
package com.project.sleep.global.config;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

/**
 * *_archive 컬렉션에 대해 _archivedAt 기반 TTL 인덱스를 보증.
 * - 운영에서 TTL_SECONDS는 구성값으로 빼는 것도 고려 가능.
 */
@Component
@RequiredArgsConstructor
public class ArchiveTtlIndexInitializerConfig {

    private final MongoTemplate mongo;

    private static final long TTL_SECONDS = 24 * 60 * 60; // 1일
    private static final String TTL_FIELD = "_archivedAt";
    private static final List<String> ARCHIVE_COLL = List.of(
            "sleep_goal_archive",
            "total_sleep_record_archive",
            "daily_sleep_record_archive",
            "daily_report_archive",
            "periodic_report_archive"
    );

    @PostConstruct
    public void ensureTtlIndexes() {
        var db = mongo.getDb();
        for (String collName : ARCHIVE_COLL) {
            var coll = db.getCollection(collName);
            boolean exists = StreamSupport.stream(coll.listIndexes().spliterator(), false)
                    .anyMatch(ix -> (TTL_FIELD + "_1").equals(String.valueOf(ix.get("name"))));
            if (!exists) {
                coll.createIndex(
                        Indexes.ascending(TTL_FIELD),
                        new IndexOptions().expireAfter(TTL_SECONDS, TimeUnit.SECONDS)
                );
            }
        }
    }
}