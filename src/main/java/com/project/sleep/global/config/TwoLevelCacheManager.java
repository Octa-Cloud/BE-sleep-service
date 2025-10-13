package com.project.sleep.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class TwoLevelCacheManager implements CacheManager {

    private final CacheManager l1CacheManager; // Caffeine
    private final CacheManager l2CacheManager; // Redis

    @Override
    public Cache getCache(String name) {
        Cache l1Cache = l1CacheManager.getCache(name);
        Cache l2Cache = l2CacheManager.getCache(name);

        return (l1Cache != null && l2Cache != null)
                ? new TwoLevelCache(name, l1Cache, l2Cache)
                : null;
    }

    @Override
    public Collection<String> getCacheNames() {
        return l1CacheManager.getCacheNames();
    }


    @Slf4j
    @RequiredArgsConstructor
    private static class TwoLevelCache implements Cache {

        private final String name;
        private final Cache l1Cache; // Caffeine (빠름)
        private final Cache l2Cache; // Redis (분산)

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getNativeCache() {
            return l1Cache.getNativeCache();
        }

        @Override
        public ValueWrapper get(Object key) {
            // 1. L1(Caffeine)에서 조회
            ValueWrapper l1Value = l1Cache.get(key);
            if (l1Value != null) {
                log.info("🎯 [L1 HIT] Cache: {}, Key: {}", name, key);
                return l1Value;
            }

            // 2. L2(Redis)에서 조회
            ValueWrapper l2Value = l2Cache.get(key);
            if (l2Value != null) {
                log.info("🌐 [L2 HIT] Cache: {}, Key: {} -> Promoting to L1", name, key);
                l1Cache.put(key, l2Value.get()); // L1에 데이터 승격
                return l2Value;
            }

            log.info("❌ [CACHE MISS] Cache: {}, Key: {}", name, key);
            return null;
        }

        @Override
        public <T> T get(Object key, Class<T> type) {
            // 이 메소드는 TwoLevelCache에서는 잘 사용되지 않으므로 기본 get을 위임합니다.
            ValueWrapper wrapper = get(key);
            return wrapper == null ? null : (T) wrapper.get();
        }

        @Override
        public <T> T get(Object key, Callable<T> valueLoader) {
            // Spring의 @Cacheable은 이 메소드를 통해 DB 조회를 실행합니다.
            ValueWrapper wrapper = get(key);
            if (wrapper != null) {
                return (T) wrapper.get();
            }

            // DB 조회
            log.info("🔥 [DB QUERY] Cache: {}, Key: {}", name, key);
            try {
                T value = valueLoader.call();
                if (value != null) {
                    put(key, value); // DB 조회 후 L1, L2에 저장
                }
                return value;
            } catch (Exception e) {
                throw new ValueRetrievalException(key, valueLoader, e);
            }
        }

        @Override
        public void put(Object key, Object value) {
            log.info("💾 [CACHE PUT] Cache: {}, Key: {} -> Storing in L1 and L2", name, key);
            l1Cache.put(key, value);
            l2Cache.put(key, value);
        }

        @Override
        public void evict(Object key) {
            log.info("🗑️ [CACHE EVICT] Cache: {}, Key: {} -> Removing from L1 and L2", name, key);
            l1Cache.evict(key);
            l2Cache.evict(key);
        }

        @Override
        public void clear() {
            log.info("🧹 [CACHE CLEAR] Cache: {} -> Clearing L1 and L2", name);
            l1Cache.clear();
            l2Cache.clear();
        }
    }
}