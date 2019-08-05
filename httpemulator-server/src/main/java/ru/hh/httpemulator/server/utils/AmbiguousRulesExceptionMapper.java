package ru.hh.httpemulator.server.utils;

import javax.annotation.Priority;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import javax.ws.rs.ext.Provider;
import ru.hh.httpemulator.server.utils.exception.AmbiguousRulesException;
import ru.hh.nab.starter.exceptions.NabExceptionMapper;
import static ru.hh.nab.starter.exceptions.NabExceptionMapper.LOW_PRIORITY;

@Provider
@Priority(LOW_PRIORITY)
public class AmbiguousRulesExceptionMapper extends NabExceptionMapper<AmbiguousRulesException> {
  public AmbiguousRulesExceptionMapper() {
    super(CONFLICT, LoggingLevel.ERROR_WITH_STACK_TRACE);
  }
}
