package com.bpfaas.starter.logger;

import com.bpfaas.starter.logger.web.IWebLogger;
import com.bpfaas.starter.logger.web.WebBasicLogger;
import com.bpfaas.starter.logger.web.WebFullLogger;
import com.bpfaas.starter.logger.web.WebHeadersLogger;
import com.bpfaas.starter.logger.web.WebNoneLogger;

public class WebLoggerFactory {

  private WebLoggerFactory() {}

  public static IWebLogger createLogger(String level) {
    IWebLogger rLogger;
    String l = null == level ? null : level.toLowerCase();
    if ("basic".equals(l)) {
      rLogger = new WebBasicLogger();
    } else if ("headers".equals(l)) {
      rLogger = new WebHeadersLogger();
    } else if ("full".equals(l)) {
      rLogger = new WebFullLogger();
    } else {
      rLogger = new WebNoneLogger();
    }

    return rLogger;
  }
}