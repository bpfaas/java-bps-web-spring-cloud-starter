package com.bpfaas.starter.config;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.bpfaas.common.utils.JsonUtils;

@ConditionalOnProperty(name = "bp.web.redis.enable", havingValue = "true")
@Component
@Configuration
@AutoConfigureAfter(WebRedisConnectionFactoryConfig.class)
public class WebRedisConfig {

  @Bean
  @RefreshScope
  ReactiveStringRedisTemplate reactiveRedisTemplate(
      @Qualifier("lettuceConnectionFactory") LettuceConnectionFactory connectionFactory) {
    return new ReactiveStringRedisTemplate(connectionFactory);
  }

  @Bean
  @RefreshScope
  ReactiveStringRedisTemplate reactiveStringRedisTemplate(
      @Qualifier("lettuceConnectionFactory") LettuceConnectionFactory connectionFactory) {
    return new ReactiveStringRedisTemplate(connectionFactory);
  }

  @Bean
  @RefreshScope
  public RedisTemplate<String, Serializable> redisTemplate(
      @Qualifier("lettuceConnectionFactory") LettuceConnectionFactory connectionFactory) {
    RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();

    // 序列化器.
    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    jackson2JsonRedisSerializer.setObjectMapper(JsonUtils.getObjectMapper());

    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
    // redisTemplate.afterPropertiesSet();

    // 使用连接池.
    redisTemplate.setConnectionFactory(connectionFactory);

    return redisTemplate;
  }

}