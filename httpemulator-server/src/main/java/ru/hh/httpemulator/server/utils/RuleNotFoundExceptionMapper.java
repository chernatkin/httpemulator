package ru.hh.httpemulator.server.utils;

import javax.annotation.Priority;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import javax.ws.rs.ext.Provider;
import ru.hh.httpemulator.server.utils.exception.RuleNotFoundException;
import ru.hh.nab.starter.exceptions.NabExceptionMapper;
import static ru.hh.nab.starter.exceptions.NabExceptionMapper.LOW_PRIORITY;

@Provider
@Priority(LOW_PRIORITY)
public class RuleNotFoundExceptionMapper extends NabExceptionMapper<RuleNotFoundException> {
  public RuleNotFoundExceptionMapper() {
    super(NOT_FOUND, NabExceptionMapper.LoggingLevel.ERROR_WITH_STACK_TRACE);
  }
}
