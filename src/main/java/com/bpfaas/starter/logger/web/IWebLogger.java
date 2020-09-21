package com.bpfaas.starter.logger.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * logger.
 */
public interface IWebLogger {
  default void logRequest(Logger logger, HttpServletRequest request, HttpServletResponse response) {
  }

  default void logResponse(Logger logger, HttpServletRequest request, HttpServletResponse response, long interval) {
  }
}