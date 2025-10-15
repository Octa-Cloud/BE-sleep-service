package com.project.sleep.global.config;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class ReplicaMongoTemplateConfig {
    private static final String CONNECTION_STRING =
            "mongodb://mongo1:27017,mongo2:27017,mongo3:27017/mong?replicaSet=rs0";

    @Bean(name = "mongoTemplate")
    @Primary
    public MongoTemplate mongoTemplate() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                .readPreference(ReadPreference.secondaryPreferred())  // 읽기 성능 최적화
                .writeConcern(WriteConcern.MAJORITY)
                .readConcern(ReadConcern.MAJORITY)
                .build();
        return new MongoTemplate(
                new SimpleMongoClientDatabaseFactory(MongoClients.create(settings), "mong")
        );
    }

    @Bean(name = "mongoTemplateWrite")
    public MongoTemplate mongoTemplateWrite() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                .readPreference(ReadPreference.primary())
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        return new MongoTemplate(
                new SimpleMongoClientDatabaseFactory(MongoClients.create(settings), "mong")
        );
    }

    @Bean(name = "mongoTemplateRead")
    public MongoTemplate mongoTemplateRead() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                .readPreference(ReadPreference.secondaryPreferred())
                .readConcern(ReadConcern.MAJORITY)
                .build();
        return new MongoTemplate(
                new SimpleMongoClientDatabaseFactory(MongoClients.create(settings), "mong")
        );
    }
}
