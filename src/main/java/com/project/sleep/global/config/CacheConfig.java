package com.project.sleep.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
// @EnableCaching은 Spring Boot가 자동으로 처리하므로 생략 가능합니다.
public class CacheConfig {

    /**
     * L1 캐시 (Caffeine) 매니저 생성
     */
    @Bean
    public CacheManager caffeineCacheManager(MeterRegistry meterRegistry) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<CaffeineCache> caches = Arrays.asList(
                buildCaffeineCache("sleep-patterns", 30, 1000),
                buildCaffeineCache("sleep-goal", 30, 1000)
        );

        cacheManager.setCaches(caches);

        caches.forEach(cache -> CaffeineCacheMetrics.monitor(meterRegistry, cache.getNativeCache(), cache.getName()));
        log.info("✅ Caffeine L1 Cache Manager initialized and instrumented");

        return cacheManager;
    }

    /**
     * L2 캐시 (Redis) 매니저 생성
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // ... (이전 코드와 동일) ...
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY);
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("sleep-patterns", defaultConfig.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("sleep-goal", defaultConfig.entryTtl(Duration.ofHours(1)))
                .enableStatistics()
                .build();

        log.info("✅ Redis L2 Cache Manager initialized with statistics enabled");
        return redisCacheManager;
    }

    /**
     * L1+L2를 함께 사용하는 2단계 캐시 매니저 생성
     */
    @Bean
    @Primary
    public CacheManager twoLevelCacheManager(
            CacheManager caffeineCacheManager,
            CacheManager redisCacheManager
    ) {
        log.info("🚀 Two-Level Cache Manager activated");
        return new TwoLevelCacheManager(caffeineCacheManager, redisCacheManager);
    }

    // CaffeineCache 객체를 반환하도록 타입 변경
    private CaffeineCache buildCaffeineCache(String name, int expireMinutes, int maximumSize) {
        return new CaffeineCache(name,
                Caffeine.newBuilder()
                        .expireAfterWrite(expireMinutes, TimeUnit.MINUTES)
                        .maximumSize(maximumSize)
                        .recordStats()
                        .build()
        );
    }
}