package ru.hh.httpemulator.server.utils;

import javax.annotation.Priority;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import javax.ws.rs.ext.Provider;
import ru.hh.httpemulator.server.utils.exception.ScenarioNotFoundException;
import ru.hh.nab.starter.exceptions.NabExceptionMapper;
import static ru.hh.nab.starter.exceptions.NabExceptionMapper.LOW_PRIORITY;

@Provider
@Priority(LOW_PRIORITY)
public class ScenarioNotFoundExceptionMapper extends NabExceptionMapper<ScenarioNotFoundException> {
  public ScenarioNotFoundExceptionMapper() {
    super(NOT_FOUND, LoggingLevel.ERROR_WITH_STACK_TRACE);
  }
}
