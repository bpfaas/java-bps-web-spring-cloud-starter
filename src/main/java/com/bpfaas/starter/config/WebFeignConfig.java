/**
* Copyright (c) 2020 Copyright bp All Rights Reserved.
* Author: brainpoint
* Date: 2020-07-08 15:34
* Desc: 
*/
package com.bpfaas.starter.config;

import com.bpfaas.starter.WebFeignDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bpfaas.common.utils.JsonUtils;
import feign.Logger.Level;
import feign.codec.Decoder;

@ConditionalOnProperty(name = "bp.web.enable", havingValue = "true")
@Configuration
@RefreshScope
public class WebFeignConfig {

  @Value("${bp.web.logging.feignClientLevel:none}")
  private String logFeignClientLevel;

  // @Bean
  // JacksonAnnotationIntrospector jacksonAnnotationIntrospector() {
  // return new JacksonAnnotationIntrospector() {
  // private static final long serialVersionUID = 1L;

  // @Override
  // public Object findSerializer(Annotated a) {
  // if (a instanceof AnnotatedMethod) {
  // AnnotatedElement m = a.getAnnotated();
  // JsonFormat an = m.getAnnotation(JsonFormat.class);
  // if (an != null) {
  // return new JackJsonDateSerializer(an.pattern());
  // }

  // DateTimeFormat dtm = m.getAnnotation(DateTimeFormat.class);
  // if (dtm != null) {
  // return new JackJsonDateSerializer(dtm.pattern());
  // }
  // }

  // return super.findSerializer(a);
  // }
  // };
  // }

  @Bean
  Level feignLoggerLevel() {
    String l = null == logFeignClientLevel ? null : logFeignClientLevel.toLowerCase();

    if ("full".equals(l)) {
      return Level.FULL;
    } else if ("basic".equals(l)) {
      return Level.BASIC;
    } else if ("headers".equals(l)) {
      return Level.HEADERS;
    } else {
      return Level.NONE;
    }
  }

  @Bean
  public Decoder feignDecoder() {
    return new WebFeignDecoder(new SpringDecoder(feignHttpMessageConverter()), JsonUtils.getObjectMapper());
  }

  public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
    final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(
        new WebFeignMappingJackson2HttpMessageConverter());
    return new ObjectFactory<HttpMessageConverters>() {
      @Override
      public HttpMessageConverters getObject() {
        return httpMessageConverters;
      }
    };
  }
}

/**
 * date serializer.
 */
// class JackJsonDateSerializer extends JsonSerializer<Date> {
// private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

// public JackJsonDateSerializer() {
// }

// public JackJsonDateSerializer(String format) {
// df = new SimpleDateFormat(format);
// }

// public void serialize(Date arg0, JsonGenerator arg1, SerializerProvider arg2)
// throws IOException, JsonProcessingException {
// if (null != arg0) {
// arg1.writeString(this.df.format(arg0));
// }
// }
// }