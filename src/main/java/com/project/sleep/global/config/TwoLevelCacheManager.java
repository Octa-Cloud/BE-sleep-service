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

    /**
     * 2단계 캐시 래퍼
     */
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
                log.debug("🎯 [L1 HIT] Cache: {}, Key: {}", name, key);
                return l1Value;
            }

            // 2. L2(Redis)에서 조회
            try {
                ValueWrapper l2Value = l2Cache.get(key);
                if (l2Value != null) {
                    log.debug("🌐 [L2 HIT] Cache: {}, Key: {} - Promoting to L1", name, key);
                    // L1에 데이터 승격
                    l1Cache.put(key, l2Value.get());
                    return l2Value;
                }
            } catch (Exception e) {
                log.warn("⚠️ [L2 ERROR] Cache: {}, Key: {} - Redis error: {}", name, key, e.getMessage());
                // L2 에러 발생 시 L1만 사용하고 계속 진행
            }

            log.debug("❌ [CACHE MISS] Cache: {}, Key: {}", name, key);
            return null;
        }

        @Override
        public <T> T get(Object key, Class<T> type) {
            // L1 조회
            T l1Value = l1Cache.get(key, type);
            if (l1Value != null) {
                log.debug("🎯 [L1 HIT] Cache: {}, Key: {}", name, key);
                return l1Value;
            }

            // L2 조회
            T l2Value = l2Cache.get(key, type);
            if (l2Value != null) {
                log.debug("🌐 [L2 HIT] Cache: {}, Key: {} - Promoting to L1", name, key);
                l1Cache.put(key, l2Value);
                return l2Value;
            }

            log.debug("❌ [CACHE MISS] Cache: {}, Key: {}", name, key);
            return null;
        }

        @Override
        public <T> T get(Object key, Callable<T> valueLoader) {
            // L1 조회
            T l1Value = l1Cache.get(key, (Class<T>) null);
            if (l1Value != null) {
                log.debug("🎯 [L1 HIT] Cache: {}, Key: {}", name, key);
                return l1Value;
            }

            // L2 조회
            T l2Value = l2Cache.get(key, (Class<T>) null);
            if (l2Value != null) {
                log.debug("🌐 [L2 HIT] Cache: {}, Key: {} - Promoting to L1", name, key);
                l1Cache.put(key, l2Value);
                return l2Value;
            }

            // DB 조회
            try {
                log.debug("🔥 [DB QUERY] Cache: {}, Key: {}", name, key);
                T value = valueLoader.call();
                if (value != null) {
                    put(key, value);
                }
                return value;
            } catch (Exception e) {
                throw new ValueRetrievalException(key, valueLoader, e);
            }
        }

        @Override
        public void put(Object key, Object value) {
            log.debug("💾 [CACHE PUT] Cache: {}, Key: {} - Storing in L1 and L2", name, key);
            // L1과 L2 모두에 저장
            l1Cache.put(key, value);
            l2Cache.put(key, value);
        }

        @Override
        public void evict(Object key) {
            log.debug("🗑️ [CACHE EVICT] Cache: {}, Key: {} - Removing from L1 and L2", name, key);
            // L1과 L2 모두에서 제거
            l1Cache.evict(key);
            l2Cache.evict(key);
        }

        @Override
        public void clear() {
            log.debug("🧹 [CACHE CLEAR] Cache: {} - Clearing L1 and L2", name);
            // L1과 L2 모두 클리어
            l1Cache.clear();
            l2Cache.clear();
        }
    }
}