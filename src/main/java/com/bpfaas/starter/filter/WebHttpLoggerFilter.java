/**
* Copyright (c) 2020 Copyright bp All Rights Reserved.
* Author: brainpoint
* Date: 2020-07-09 15:22
* Desc: 
*/

package com.bpfaas.starter.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bpfaas.starter.WebHttpBodyServletRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.bpfaas.starter.WebHttpBodyServletResponseWrapper;
import com.bpfaas.starter.logger.web.IWebLogger;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "bp.web.enable", havingValue = "true")
@Component
@Slf4j
public class WebHttpLoggerFilter implements Filter {

  @Autowired
  private IWebLogger webLogger;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    WebHttpBodyServletResponseWrapper wrapResponse = new WebHttpBodyServletResponseWrapper(
        (HttpServletResponse) response);
    WebHttpBodyServletRequestWrapper wrapRequest = new WebHttpBodyServletRequestWrapper((HttpServletRequest) request);

    // log.
    long interval = System.nanoTime();
    webLogger.logRequest(log, (HttpServletRequest) wrapRequest, (HttpServletResponse) wrapResponse);

    // chain.
    chain.doFilter(wrapRequest, wrapResponse);

    // log.
    interval = (System.nanoTime() - interval) / 1000000;
    webLogger.logResponse(log, wrapRequest, wrapResponse, interval);
  }
}