package org.example.observability.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisCacheConfiguration redisCacheConfiguration,
                                     RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .enableStatistics();
        return redisCacheManagerBuilder.build();
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(2))
                .disableCachingNullValues();
    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisConfiguration redisConfiguration = LettuceConnectionFactory.createRedisConfiguration("jdbc:redis://192.168.1.76:6379/0");
//        return new LettuceConnectionFactory(redisConfiguration);
//    }

}
