// com.project.sleep.global.config.MongoTxConfig.java
package com.project.sleep.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/** Mongo 트랜잭션 매니저. TransactionTemplate 에서 사용. */
@Configuration
public class MongoTxConfig {
    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}