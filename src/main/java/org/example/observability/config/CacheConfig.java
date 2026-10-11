package org.example.observability.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * Configuration for multi-tiered caching strategies in the application.
 * <p>
 * Configures:
 * <ul>
 *   <li>Distributed Redis caching as the primary {@link CacheManager} with custom TTL, idle expiration, and JSON serialization.</li>
 *   <li>Local in-memory caching via {@link com.github.benmanes.caffeine.cache.Caffeine} for fast micro-caching.</li>
 *   <li>A {@link CompositeCacheManager} combining both Redis and Caffeine managers for hierarchical or heterogeneous cache routing.</li>
 * </ul>
 */
@Configuration
public class CacheConfig {

    /**
     * Configures the primary {@link RedisCacheManager} bean using the provided Redis connection factory and defaults.
     * <p>
     * Enables cache statistics to monitor hit/miss ratios via Micrometer and Actuator metrics endpoints.
     *
     * @param redisCacheConfiguration the base Redis configuration (TTL, serializers, null handling)
     * @param redisConnectionFactory  the underlying connection factory (Lettuce) for Redis operations
     * @return the primary {@link CacheManager} backed by Redis
     */
    @Primary
    @Bean
    public CacheManager cacheManager(RedisCacheConfiguration redisCacheConfiguration,
                                     RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .enableStatistics()
                .build();
        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .enableStatistics();
        return redisCacheManagerBuilder.build();
    }

    /**
     * Defines default cache settings for Redis entries.
     * <p>
     * Configures:
     * <ul>
     *   <li>Entry Time-To-Live (TTL): 1 minute.</li>
     *   <li>Time-To-Idle (TTI): Resets TTL back to 1 minute on each key access.</li>
     *   <li>Null caching: Disabled to prevent caching empty responses.</li>
     *   <li>Serialization: String serialization for keys and JSON serialization for values.</li>
     * </ul>
     *
     * @return the configured {@link RedisCacheConfiguration}
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .enableTimeToIdle()             // Resets TTL back to 1 min every time the key is read
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
    }





    /**
     * Builds a Caffeine cache builder with write expiration, maximum capacity, and metrics recording.
     *
     * @return the configured {@link Caffeine} cache builder instance
     */
    private Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(60))
                .maximumSize(10)
                .recordStats();
    }




    // Caffeine Setup
    /**
     * Configures an in-memory {@link CaffeineCacheManager} bean.
     * Useful for lightweight, low-latency caching that avoids remote network round-trips.
     *
     * @return the {@link CaffeineCacheManager} bean backed by Caffeine
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(caffeine());
        return manager;
    }




    /**
     * Configures a composite {@link CacheManager} that delegates to multiple cache providers in priority order.
     *
     * @param redis    the primary distributed Redis cache manager
     * @param caffeine the secondary local Caffeine cache manager
     * @return a {@link CompositeCacheManager} wrapping both cache providers with no-op fallback disabled
     */
    @Bean
    public CacheManager compositeCacheManager(@Qualifier("cacheManager") CacheManager redis, @Qualifier("caffeineCacheManager") CacheManager caffeine) {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(redis, caffeine);
        compositeCacheManager.setFallbackToNoOpCache(false);
        return compositeCacheManager;
    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisConfiguration redisConfiguration = LettuceConnectionFactory.createRedisConfiguration("jdbc:redis://192.168.1.76:6379/0");
//        return new LettuceConnectionFactory(redisConfiguration);
//    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
//        standaloneConfiguration.setHostName("192.168.1.76");
//        standaloneConfiguration.setUsername("default");
//        standaloneConfiguration.setPassword("root123");
//        return new LettuceConnectionFactory(standaloneConfiguration);
//    }

//    @Bean
//    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//
////        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
////        redisTemplate.setKeySerializer(genericJackson2JsonRedisSerializer);
////        redisTemplate.setHashKeySerializer(genericJackson2JsonRedisSerializer);
////        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
////        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
//
//        return redisTemplate;
//    }


}
