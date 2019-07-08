package ru.hh.httpemulator.server.resources;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.engine.CriteriaHttpEngine;
import ru.hh.httpemulator.server.engine.ScenarioEngine;
import ru.hh.httpemulator.server.utils.HttpUtils;
import ru.hh.httpemulator.server.utils.exception.AmbiguousRulesException;
import ru.hh.httpemulator.server.utils.exception.RuleNotFoundException;
import ru.hh.httpemulator.server.utils.exception.ScenarioNotFoundException;

@Path("/")
public class RequestController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

  private final ScenarioEngine scenarioEngine;
  private final CriteriaHttpEngine criteriaEngine;

  @Inject
  public RequestController(ScenarioEngine scenarioEngine,
                           CriteriaHttpEngine criteriaEngine) {
    this.scenarioEngine = scenarioEngine;
    this.criteriaEngine = criteriaEngine;
  }

  @GET
  @Path("{any: .*}")
  public void processGet(@PathParam("any") List<PathSegment> segments,
                         @Context HttpServletRequest request,
                         @Context HttpServletResponse response)
      throws IOException, AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    process(request, response);
  }

  @POST
  @Path("{any: .*}")
  public void processPost(@PathParam("any") List<PathSegment> segments,
                          @Context HttpServletRequest request,
                          @Context HttpServletResponse response)
      throws IOException, AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    process(request, response);
  }

  @PUT
  @Path("{any: .*}")
  public void processPut(@PathParam("any") List<PathSegment> segments,
                            @Context HttpServletRequest request,
                            @Context HttpServletResponse response)
      throws IOException, AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    process(request, response);
  }

  @DELETE
  @Path("{any: .*}")
  public void processDelete(@PathParam("any") List<PathSegment> segments,
                            @Context HttpServletRequest request,
                            @Context HttpServletResponse response)
      throws IOException, AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    process(request, response);
  }

  @HEAD
  @Path("{any: .*}")
  public void processHead(@PathParam("any") List<PathSegment> segments,
                            @Context HttpServletRequest request,
                            @Context HttpServletResponse response)
      throws IOException, AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    process(request, response);
  }

  @OPTIONS
  @Path("{any: .*}")
  public void processOptions(@PathParam("any") List<PathSegment> segments,
                            @Context HttpServletRequest request,
                            @Context HttpServletResponse response)
      throws IOException, AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    process(request, response);
  }

  @PATCH
  @Path("{any: .*}")
  public void processPatch(@PathParam("any") List<PathSegment> segments,
                            @Context HttpServletRequest request,
                            @Context HttpServletResponse response)
      throws IOException, AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    process(request, response);
  }

  private void process(HttpServletRequest request, HttpServletResponse response)
      throws AmbiguousRulesException, RuleNotFoundException, IOException, ScenarioNotFoundException {
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

  private long getRequestId(HttpServletRequest request) {
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

  private void convertToHttpResponse(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Collection<HttpEntry> entries) throws IOException, ScenarioNotFoundException {
    for (HttpEntry httpEntry : entries) {
      switch (httpEntry.getType()) {
        case BODY:
          response.getOutputStream().write(httpEntry.getValue().getBytes(Charset.forName("UTF-8")));
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
          break;
        default:
          break;
      }
    }
  }
}
