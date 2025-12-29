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
                .entryTtl(Duration.ofMinutes(10))
                .enableTimeToIdle()
                .disableCachingNullValues();
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
//    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<Object, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
//        stringRedisTemplate.setConnectionFactory(connectionFactory);
//        return stringRedisTemplate;
//    }

}
