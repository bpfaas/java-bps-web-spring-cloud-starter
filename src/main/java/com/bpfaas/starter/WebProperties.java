/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved.
 * Author: brainpoint
 * Date: 2020-2020/6/24 15:17
 * Desc:
 */
package com.bpfaas.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author brainpoint
 * @date 2020/6/24 3:17 下午
 */
@Data
@ConfigurationProperties(prefix = "bp.web")
public class WebProperties {
    /**
     * use bp-web-spring-boot-starter to make response package. It cannot be
     * refreshed dynamically.
     **/
    protected boolean enable;

    @Data
    @ConfigurationProperties(prefix = "bp.web.logging")
    public static class Logging {

        /**
         * Logging feign client request. (cannot be dynamic refresh)
         * 
         * <p>
         * - none: no logging.
         * </p>
         * <p>
         * - basic: Log only the request method and URL and the response status code and
         * execution time.
         * </p>
         * <p>
         * - headers: Log the basic information along with request and response headers.
         * </p>
         * <p>
         * - full: Log the headers, body, and metadata for both requests and responses.
         * </p>
         */
        protected String feignClientLevel;

        /**
         * Logging level.
         * 
         * <p>
         * - none: no logging.
         * </p>
         * <p>
         * - basic: Log only the request method and URL and the response status code and
         * execution time.
         * </p>
         * <p>
         * - headers: Log the basic information along with request and response headers.
         * </p>
         * <p>
         * - full: Log the headers, body, and metadata for both requests and responses.
         * </p>
         */
        protected String level;
    }

    @Data
    @ConfigurationProperties(prefix = "bp.web.redis")
    public static class Redis {
        /**
         * enable redis.
         */
        protected boolean enable;
    }
}