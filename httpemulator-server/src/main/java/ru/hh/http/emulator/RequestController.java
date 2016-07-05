package ru.hh.http.emulator;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.hh.http.emulator.client.entity.HttpEntry;
import ru.hh.http.emulator.engine.CriteriaHttpEngine;
import ru.hh.http.emulator.engine.ScenarioEngine;
import ru.hh.http.emulator.exception.AmbiguousRulesException;
import ru.hh.http.emulator.exception.RuleNotFoundException;
import ru.hh.http.emulator.exception.ScenarioNotFoundException;
import ru.hh.http.emulator.utils.CharsetUtils;
import ru.hh.http.emulator.utils.HttpUtils;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequestMapping("/**")
public class RequestController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

  @Autowired
  private ScenarioEngine scenarioEngine;

  @Autowired
  private CriteriaHttpEngine criteriaEngine;

  @RequestMapping(
    method =
    {
      RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.PATCH,
      RequestMethod.TRACE
    }
  )
  @ResponseBody
  public void process(final HttpServletRequest request, final HttpServletResponse response) throws AmbiguousRulesException, RuleNotFoundException,
    IOException, ScenarioNotFoundException {
    final long requestId = getRequestId(request);

    final Collection<HttpEntry> requestEntries = HttpUtils.convertToHttpEntries(request);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("EMULATOR REQUEST. id=" + requestId + "\n" + requestEntries);
    }

    final Collection<HttpEntry> responseEntries = criteriaEngine.process(requestEntries);
    convertToHttpResponse(request, response, responseEntries);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("EMULATOR RESPONSE. id=" + requestId + "\n" + responseEntries);
    }
  }

  private long getRequestId(final HttpServletRequest request) {
    RequestAttributes attr = RequestContextHolder.getRequestAttributes();
    if (attr == null) {
      if (request == null) {
        return -1;
      }
      attr = new ServletRequestAttributes(request);
      RequestContextHolder.setRequestAttributes(attr);
    }

    Long requestId = (Long) attr.getAttribute(HttpUtils.REQUEST_ID_PARAM_NAME, ServletRequestAttributes.SCOPE_REQUEST);
    if (requestId == null) {
      requestId = HttpUtils.nextRequestId();
      attr.setAttribute(HttpUtils.REQUEST_ID_PARAM_NAME, requestId, ServletRequestAttributes.SCOPE_REQUEST);
    }

    return requestId;
  }

  private long getRequestId() {
    return getRequestId(null);
  }

  public void convertToHttpResponse(final HttpServletRequest request, final HttpServletResponse response, final Collection<HttpEntry> entries)
    throws IOException, ScenarioNotFoundException {
    for (HttpEntry httpEntry : entries) {
      switch (httpEntry.getType()) {
        case BODY:
          response.getOutputStream().write(httpEntry.getValue().getBytes(CharsetUtils.UTF_8));
          break;
        case HEADER:
          response.addHeader(httpEntry.getKey(), httpEntry.getValue());
          break;
        case COOKIE:
          response.addCookie(new Cookie(httpEntry.getKey(), httpEntry.getValue()));
          break;
        case STATUS:
          response.setStatus(Integer.parseInt(httpEntry.getValue()));
          break;
        case SCENARIO:
          scenarioEngine.executeScenario(httpEntry.getValue(), request, response, entries);
        default:
          break;
      }
    }
  }

  @ExceptionHandler({ AmbiguousRulesException.class })
  @ResponseBody
  @ResponseStatus(HttpStatus.CONFLICT)
  public String badRequest(AmbiguousRulesException e) {
    LOGGER.warn("EMULATOR RESPONSE. id=" + getRequestId() + ", status=" + HttpStatus.CONFLICT, e);
    return e.getMessage();
  }

  @ExceptionHandler({ RuleNotFoundException.class, ScenarioNotFoundException.class })
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void notFound(RuleNotFoundException e) {
    LOGGER.warn("EMULATOR RESPONSE. id=" + getRequestId() + ", status=" + HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String internalFail(Exception e) {
    LOGGER.warn("EMULATOR RESPONSE. id=" + getRequestId() + ", status=" + HttpStatus.INTERNAL_SERVER_ERROR, e);
    return e.getMessage();
  }
}
