/**
* Copyright (c) 2020 Copyright bp All Rights Reserved.
* Author: brainpoint
* Date: 2020-07-09 00:40
* Desc: 
*/
package com.bpfaas.starter.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bpfaas.common.utils.JsonUtils;
import com.bpfaas.starter.WebHandlerInterceptorAdapter;

@ConditionalOnProperty(name = "bp.web.enable", havingValue = "true")
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Autowired
  private CorsInterceptor corsInterceptor;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("*")
            .allowCredentials(true)
            .allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .maxAge(3600);
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
    Map<Class<?>, JsonSerializer<?>> map = new HashMap<>();
    map.put(Long.class, ToStringSerializer.instance);
    return builder -> builder.serializersByType(map);
  }

  @Bean
  ObjectMapper objectMapper() {
    return JsonUtils.getObjectMapper();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new WebHandlerInterceptorAdapter()).addPathPatterns("/**");
    registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
  }

  /**
   * 防止单字符串返回是解析为 "string".
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    WebMvcMappingJackson2HttpMessageConverter converter = new WebMvcMappingJackson2HttpMessageConverter();

    // StringConverter 必须在第一位
    converters.add(0, converter);
    converters.add(0, new StringHttpMessageConverter());
  }
}