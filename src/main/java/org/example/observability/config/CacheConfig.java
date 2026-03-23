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

@Configuration
public class CacheConfig {

    @Primary
    @Bean
    public CacheManager cacheManager(RedisCacheConfiguration redisCacheConfiguration,
                                     RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .enableStatistics();
        return redisCacheManagerBuilder.build();
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .enableTimeToIdle()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
    }

    private Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(10))
                .maximumSize(10)
                .recordStats();
    }

    // Caffeine setup
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(caffeine());
        return manager;
    }

    // Multiple Cache setup
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
