/**
 * Copyright (c) 2020 Copyright bp All Rights Reserved. Author: brian.li Date:
 * 2020-07-21 19:26 Desc:
 */
package com.bpfaas.starter.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import com.bpfaas.common.utils.JsonUtils;

public class WebMvcMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

  public WebMvcMappingJackson2HttpMessageConverter() {

    setObjectMapper(JsonUtils.getObjectMapper());

    LinkedList<MediaType> mediaList = new LinkedList<>();
    mediaList.add(MediaType.TEXT_HTML);
    mediaList.add(MediaType.APPLICATION_JSON);

    this.setSupportedMediaTypes(mediaList);
  }

  @Override
  protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException {
    if (object instanceof String) {

      Charset charset = Charset.defaultCharset();
      MediaType contentType = outputMessage.getHeaders().getContentType();
      if (contentType != null && contentType.getCharset() != null) {
        charset = contentType.getCharset();
      }

      StreamUtils.copy((String) object, charset, outputMessage.getBody());
    } else {
      outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      super.writeInternal(object, type, outputMessage);
    }
  }
}