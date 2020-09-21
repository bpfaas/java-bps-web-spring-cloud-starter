/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bpfaas.starter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;

import com.bpfaas.common.utils.FileUtils;
import com.bpfaas.common.web.Msg;
import com.bpfaas.common.web.Response;
import feign.FeignException;
import feign.codec.Decoder;

/**
 * Decoder adds compatibility for Spring MVC's ResponseEntity to any other
 * decoder via composition.
 * 
 * From ResponseEntityDecoder.
 */
public class WebFeignDecoder implements Decoder {

	private Decoder decoder;
	private ObjectMapper objectMapper;

	public WebFeignDecoder(Decoder decoder, ObjectMapper objectMapper) {
		this.decoder = decoder;
		this.objectMapper = objectMapper;
	}

	@Override
	public Object decode(final feign.Response response, Type type) throws IOException, FeignException {

		if (isParameterizeHttpEntity(type)) {
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			Object decodedObject = this.decoder.decode(response, type);

			return createResponse(decodedObject, response);
		} else if (isHttpEntity(type)) {
			return createResponse(null, response);
		} else {
			if (response.body() == null) {
				return null;
			}

			Collection<String> contentType = response.headers().get("Content-Type");
			if (null == contentType) {
				try (InputStream is = response.body().asInputStream()) {
					String str = FileUtils.readFile(is, StandardCharsets.UTF_8.name());
					return createResponse(str, response);
				}
			}

			//
			// json.
			//
			for (Iterator<String> itr = contentType.iterator(); itr.hasNext();) {
				String v = itr.next();
				if (v.indexOf("application/json") >= 0) {
					Object o = getDecodeBodyObject(response, type);
					if (o != null) {
						return o;
					} else {
						try (InputStream is = response.body().asInputStream()) {
							return this.objectMapper.readValue(is, (Class<?>) type);
						}
					}
				}
			}

			//
			// Raw.
			//
			Object o = getDecodeBodyObject(response, type);
			if (o != null) {
				return o;
			} else {
				try (InputStream is = response.body().asInputStream()) {
					String str = FileUtils.readFile(is, StandardCharsets.UTF_8.name());
					return createResponse(str, response);
				}
			}
		}
	}

	/**
	 * 获得解码后的类型.
	 * 
	 * @param response
	 * @param type
	 * @return 如果返回null, 表示找不到匹配的类型; 外部应该使用 response.body().
	 * @throws IOException
	 */
	private Object getDecodeBodyObject(final feign.Response response, Type type) throws IOException {
		if (type instanceof ParameterizedType) {
			// TODO: 模板类, 不使用 application/json, 不支持反序列化; 此处会抛出异常.
			final ParameterizedType t = (ParameterizedType) type;

			TypeReference<?> typeReference = new TypeReference<Msg<?>>() {
				@Override
				public Type getType() {
					return t;
				}
			};

			return this.objectMapper.readValue(response.body().asInputStream(), typeReference);
		} else if (isResponseEntity(type)) {
			try (InputStream is = response.body().asInputStream()) {
				String str = FileUtils.readFile(is, StandardCharsets.UTF_8.name());
				return createResponse(str, response);
			}
		} else {
			return null;
		}
	}

	private boolean isParameterizeHttpEntity(Type type) {
		if (type instanceof ParameterizedType) {
			return isHttpEntity(((ParameterizedType) type).getRawType());
		}
		return false;
	}

	private boolean isHttpEntity(Type type) {
		if (type instanceof Class) {
			Class<?> c = (Class<?>) type;
			return HttpEntity.class.isAssignableFrom(c);
		}
		return false;
	}

	private boolean isResponseEntity(Type type) {
		if (type instanceof Class) {
			Class<?> c = (Class<?>) type;
			return Response.class.isAssignableFrom(c);
		}
		return false;
	}

	private com.bpfaas.common.web.Response createResponse(Object body, feign.Response response) {

		com.bpfaas.common.web.Response res = new com.bpfaas.common.web.Response();

		// headers.
		for (String key : response.headers().keySet()) {
			res.setHeader(key, response.headers().get(key));
		}

		// statusCode.
		res.setStatusCode(response.status());

		// body.
		// res.setBody(response.body());
		res.setBody(body);

		return res;
	}

}
