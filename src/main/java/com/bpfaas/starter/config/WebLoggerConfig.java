package com.bpfaas.starter.config;

import com.bpfaas.starter.logger.WebLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.bpfaas.starter.logger.web.IWebLogger;

@ConditionalOnProperty(name = "bp.web.enable", havingValue = "true")
@Component
@Configuration
@RefreshScope
public class WebLoggerConfig {

  @Value("${bp.web.logging.level:none}")
  private String level;

  @Bean
  @RefreshScope
  IWebLogger webLogger() {
    return WebLoggerFactory.createLogger(level);
  }
}