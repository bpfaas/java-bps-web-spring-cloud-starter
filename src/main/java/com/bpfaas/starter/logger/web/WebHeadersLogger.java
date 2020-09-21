package com.bpfaas.starter.logger.web;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bpfaas.starter.WebHttpBodyServletResponseWrapper;
import org.slf4j.Logger;

public class WebHeadersLogger extends WebBasicLogger {
  @Override
  public void logRequest(Logger logger, HttpServletRequest request, HttpServletResponse response) {
    super.logRequest(logger, request, response);
    logRequestHeaders(logger, request, request);

    logger.info("[{}] ---> END HTTP ({}-byte body)", request.getRemoteHost(),
        logRequestContent(logger, request, response));
  }

  @Override
  public void logResponse(Logger logger, HttpServletRequest request, HttpServletResponse response, long interval) {
    super.logResponse(logger, request, response, interval);

    logResponseHeaders(logger, request, response);

    int len = logResponseContent(logger, request, response, interval);
    logger.info("[{}] <--- END HTTP ({}-byte body)\n", request.getRemoteHost(), len);
  }

  protected void logRequestHeaders(Logger logger, HttpServletRequest request, HttpServletRequest re) {
    Enumeration<String> headerName = re.getHeaderNames();
    while (headerName.hasMoreElements()) {
      String next = headerName.nextElement();

      Enumeration<String> headerValue = re.getHeaders(next);
      while (headerValue.hasMoreElements()) {
        String value = headerValue.nextElement();
        logger.info("[{}] {}: {}", request.getRemoteHost(), next, value);
      }
    }
  }

  protected void logResponseHeaders(Logger logger, HttpServletRequest request, HttpServletResponse re) {
    Collection<String> headerNames = re.getHeaderNames();
    Iterator<String> itr = headerNames.iterator();
    while (itr.hasNext()) {
      String next = itr.next();

      Collection<String> headerValues = re.getHeaders(next);
      Iterator<String> itrValue = headerValues.iterator();
      while (itrValue.hasNext()) {
        String value = itrValue.next();
        logger.info("[{}] {}: {}", request.getRemoteHost(), next, value);
      }
    }
  }

  protected long logRequestContent(Logger logger, HttpServletRequest request, HttpServletResponse response) {
    return request.getContentLengthLong();
  }

  protected int logResponseContent(Logger logger, HttpServletRequest request, HttpServletResponse response,
      long interval) {
    return ((WebHttpBodyServletResponseWrapper) response).getBody().length;
  }
}