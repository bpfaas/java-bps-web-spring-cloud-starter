package com.bpfaas.starter.logger.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

public class WebBasicLogger implements IWebLogger {
  @Override
  public void logRequest(Logger logger, HttpServletRequest request, HttpServletResponse response) {
    String qs = request.getQueryString();
    if (qs != null && qs.length() > 0) {
      qs = "?" + qs;
    } else {
      qs = "";
    }
    logger.info("[{}] ---> {} {}:/{}{} HTTP/1.1", request.getRemoteHost(), request.getMethod(), request.getScheme(),
        request.getRequestURI(), qs);
  }

  @Override
  public void logResponse(Logger logger, HttpServletRequest request, HttpServletResponse response, long interval) {
    logger.info("[{}] <--- HTTP/1.1 {} ({}ms)\n", request.getRemoteHost(), response.getStatus(), interval);
  }
}