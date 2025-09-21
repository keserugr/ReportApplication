package com.keserugr.transaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);
        var stringSer = new StringRedisSerializer();
        tpl.setKeySerializer(stringSer);
        tpl.setValueSerializer(stringSer);
        tpl.setHashKeySerializer(stringSer);
        tpl.setHashValueSerializer(stringSer);
        tpl.afterPropertiesSet();
        return tpl;
    }
}
