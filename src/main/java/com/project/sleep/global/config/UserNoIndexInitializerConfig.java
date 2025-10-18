// com.project.sleep.global.config.UserNoIndexInitializerConfig.java
package com.project.sleep.global.config;

import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * 원본 컬렉션(daily_report/daily_sleep_record/periodic_report)의 user_no 인덱스를 보증.
 * - 아카이브/복구 시 user_no 조회 비용을 일정하게 유지.
 */
@Component
@RequiredArgsConstructor
class UserNoIndexInitializerConfig {
    private final MongoTemplate mongo;
    private static final List<String> BASE_COLL = List.of(
            "daily_report", "daily_sleep_record", "periodic_report"
    );

    @PostConstruct
    void ensureIndexes() {
        var db = mongo.getDb();
        for (String c : BASE_COLL) {
            var coll = db.getCollection(c);
            boolean exists = StreamSupport.stream(coll.listIndexes().spliterator(), false)
                    .anyMatch(ix -> "user_no_1".equals(String.valueOf(ix.get("name"))));
            if (!exists) {
                coll.createIndex(Indexes.ascending("user_no"));
            }
        }
    }
}