/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved. Author: brian.li Date:
 * 2020-07-21 19:26 Desc:
 */
package com.bpfaas.starter.config;

import com.bpfaas.common.utils.JsonUtils;

public class WebFeignMappingJackson2HttpMessageConverter extends WebMvcMappingJackson2HttpMessageConverter {

  public WebFeignMappingJackson2HttpMessageConverter() {
    super();
    // object mapper.
    setObjectMapper(JsonUtils.getObjectMapper());
  }
}