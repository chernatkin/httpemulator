package ru.hh.httpemulator.server.utils;

import io.sentry.logback.SentryAppender;
import ru.hh.nab.logging.HhMultiAppender;
import ru.hh.nab.starter.NabLogbackBaseConfigurator;

public class EmulatorLogbackConfigurator extends NabLogbackBaseConfigurator {

  @Override
  public void configure(LoggingContextWrapper context, HhMultiAppender service, HhMultiAppender libraries, SentryAppender sentryAppender) {

  }
}
