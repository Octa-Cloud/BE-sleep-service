package com.project.sleep.global.config;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class ReplicaMongoTemplateConfig {
    
    @Value("${SPRING_DATA_MONGODB_URI:mongodb://mongodb-0.mongodb-headless.microservices-dev.svc.cluster.local:27017,mongodb-1.mongodb-headless.microservices-dev.svc.cluster.local:27017,mongodb-2.mongodb-headless.microservices-dev.svc.cluster.local:27017/mong?replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=true}")
    private String connectionString;

    @Bean(name = "mongoTemplate")
    @Primary
    public MongoTemplate mongoTemplate() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
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
                .applyConnectionString(new ConnectionString(connectionString))
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
                .applyConnectionString(new ConnectionString(connectionString))
                .readPreference(ReadPreference.secondaryPreferred())
                .readConcern(ReadConcern.MAJORITY)
                .build();
        return new MongoTemplate(
                new SimpleMongoClientDatabaseFactory(MongoClients.create(settings), "mong")
        );
    }
}
