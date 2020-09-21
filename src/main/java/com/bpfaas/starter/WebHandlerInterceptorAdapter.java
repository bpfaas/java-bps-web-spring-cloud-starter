/**
* Copyright (c) 2020 Copyright bp All Rights Reserved.
* Author: brainpoint
* Date: 2020-07-09 00:02
* Desc: 
*/
package com.bpfaas.starter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class WebHandlerInterceptorAdapter extends HandlerInterceptorAdapter {
  public WebHandlerInterceptorAdapter() {
  }

  /**
   * This implementation always returns {@code true}.
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    return true;
  }

  /**
   * This implementation is empty.
   */
  // @Override
  // public void postHandle(HttpServletRequest request, HttpServletResponse
  // response, Object handler,
  // @Nullable ModelAndView modelAndView) throws Exception {
  // }

  /**
   * This implementation is empty.
   */
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
      @Nullable Exception ex) throws Exception {

    // rLogger.logResponse(log, request, response, interval);
  }

  // /**
  //  * This implementation is empty.
  //  */
  // @Override
  // public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
  //     throws Exception {
  //   super.afterConcurrentHandlingStarted(request, response, handler);
  // }
}
