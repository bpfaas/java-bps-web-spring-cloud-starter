/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: brainpoint
 * Date: 2020-2020/6/24 16:40
 * Desc:
 */
package com.bpfaas.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bpfaas.starter.controller.WebAdviceController;
import com.bpfaas.starter.controller.WebNotFoundController;
import lombok.extern.slf4j.Slf4j;


@ConditionalOnProperty(name = "bp.web.enable", havingValue = "true")
@Configuration
@EnableConfigurationProperties(WebProperties.class)
@Slf4j
public class WebAutoConfigure {

    public WebAutoConfigure() {
        log.info("[bp web starter] enable");
    }

    @Bean
    @ConditionalOnMissingBean
    WebAdviceController adviceController (){
        return new WebAdviceController();
    }

    @Bean
    @ConditionalOnMissingBean
    WebNotFoundController notFoundController (){
        return new WebNotFoundController();
    }
}
