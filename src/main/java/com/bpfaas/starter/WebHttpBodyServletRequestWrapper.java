/**
* Copyright (c) 2020 Copyright bp All Rights Reserved.
* Author: brainpoint
* Date: 2020-07-09 15:26
* Desc: 
*/

package com.bpfaas.starter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * use for cache requset body.
 */
public class WebHttpBodyServletRequestWrapper extends HttpServletRequestWrapper {

  private ByteArrayInputStream byteArrayInputStream;
  private HttpServletRequest request;
  private String body;

  public WebHttpBodyServletRequestWrapper(HttpServletRequest request) {
    super(request);
    this.request = request;
  }

  private void fetchBody() throws IOException {
    if (byteArrayInputStream == null) {
      StringBuilder sb = new StringBuilder("");

      try (BufferedReader bufferedReader = request.getReader()) {
        String line;
        while ((line = bufferedReader.readLine()) != null)
          sb.append(line);
      }
      this.body = sb.toString();
      this.byteArrayInputStream = new ByteArrayInputStream(this.body.getBytes(StandardCharsets.UTF_8.name()));
    }
  }

  public String getBody() throws IOException {
    fetchBody();
    return this.body;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    fetchBody();
    return new ServletInputStreamWrapper(byteArrayInputStream, request);
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(this.getInputStream()));
  }

  @AllArgsConstructor
  public static class ServletInputStreamWrapper extends ServletInputStream {

    @Setter
    @Getter
    private ByteArrayInputStream inputStream;

    @Setter
    @Getter
    private HttpServletRequest request;

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public boolean isFinished() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
    }

    @Override
    public int read() throws IOException {
      return inputStream.read();
    }
  }
}