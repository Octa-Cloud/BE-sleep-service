package com.project.sleep.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * L1 캐시 (Caffeine - 로컬 메모리)
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(Arrays.asList(
                buildCaffeineCache("dailySleepSummary", 60, 1000),
                buildCaffeineCache("recentSleepSummary", 30, 500),
                buildCaffeineCache("dailyReport", 30, 500),
                buildCaffeineCache("weeklyReport", 30, 500),
                buildCaffeineCache("monthlyReport", 30, 500),
                buildCaffeineCache("totalSleepRecord", 30, 500),
                buildCaffeineCache("sleepGoal", 30, 500),
                buildCaffeineCache("sleepPattern", 30, 500)
        ));

        log.info("✅ Caffeine L1 Cache Manager initialized");

        return cacheManager;
    }

    /**
     * L2 캐시 (Redis - 분산 캐시)
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // ObjectMapper 설정 - LocalTime, LocalDate 등 Java 8 시간 타입 지원
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 중요: 타입 정보를 포함하되, 더 관대한 검증 사용
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.EVERYTHING,  // EVERYTHING으로 변경
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                )
                .entryTtl(Duration.ofHours(1));

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("dailySleepSummary",
                        defaultConfig.entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("recentSleepSummary",
                        defaultConfig.entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("dailyReport",
                        defaultConfig.entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("weeklyReport",
                        defaultConfig.entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("monthlyReport",
                        defaultConfig.entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("totalSleepRecord",
                        defaultConfig.entryTtl(Duration.ofHours(6)))
                .withCacheConfiguration("sleepGoal",
                        defaultConfig.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("sleepPattern",
                        defaultConfig.entryTtl(Duration.ofHours(6)))
                .build();

        log.info("✅ Redis L2 Cache Manager initialized");

        return redisCacheManager;
    }

    /**
     * L1+L2를 함께 사용하는 2단계 캐시 매니저 생성
     */
    @Bean
    @Primary  // 이 매니저를 기본으로 사용
    public CacheManager twoLevelCacheManager(
            CacheManager caffeineCacheManager,
            CacheManager redisCacheManager
    ) {
        log.info("🚀 Two-Level Cache Manager activated");
        return new TwoLevelCacheManager(caffeineCacheManager, redisCacheManager);
    }


    // CaffeineCache 객체를 반환하도록 타입 변경
    private Cache buildCaffeineCache(String name, int expireMinutes, int maximumSize) {
        return new CaffeineCache(name,
                Caffeine.newBuilder()
                        .expireAfterWrite(expireMinutes, TimeUnit.MINUTES)
                        .maximumSize(maximumSize)
                        .recordStats()
                        .build()
        );
    }
}