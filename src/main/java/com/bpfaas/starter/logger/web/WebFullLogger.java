package com.bpfaas.starter.logger.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bpfaas.starter.WebHttpBodyServletRequestWrapper;
import org.slf4j.Logger;

import com.bpfaas.starter.WebHttpBodyServletResponseWrapper;

public class WebFullLogger extends WebHeadersLogger {

  static final int MAX_CONTENT_LENGTH = 1024;

  @Override
  protected long logRequestContent(Logger logger, HttpServletRequest request, HttpServletResponse response) {

    long len = 0;

    try {

      WebHttpBodyServletRequestWrapper req = (WebHttpBodyServletRequestWrapper) request;

      String sb = req.getBody();
      len = sb.length();

      if (len > 0) {
        logger.info("[{}]", request.getRemoteHost());
        logger.info("[{}] {}", request.getRemoteHost(), sb);
      }

    } catch (IOException e) {
      len = -1;
      e.printStackTrace();
    }

    return len;
  }

  @Override
  public int logResponseContent(Logger logger, HttpServletRequest request, HttpServletResponse response,
      long interval) {
    int len = -1;
    try {
      WebHttpBodyServletResponseWrapper responseWrapper = (WebHttpBodyServletResponseWrapper) response;
      byte[] responseWrapperContentAsByteArray = responseWrapper.getBody();

      String content;
      if (responseWrapperContentAsByteArray != null) {
        len = responseWrapperContentAsByteArray.length;

        if (len > MAX_CONTENT_LENGTH) {
          content = new String(responseWrapperContentAsByteArray, 0, MAX_CONTENT_LENGTH, StandardCharsets.UTF_8.name());
          content = content + "...";
        } else {
          content = new String(responseWrapperContentAsByteArray, StandardCharsets.UTF_8.name());
        }

        if (content.length() > 0) {
          logger.info("[{}]", request.getRemoteHost());
          logger.info("[{}] {}", request.getRemoteHost(), content);
        }
      }
    } catch (Exception ex) {
      logger.info("[{}]-ReadContentError", request.getRemoteHost());
    }

    return len;
  }
}