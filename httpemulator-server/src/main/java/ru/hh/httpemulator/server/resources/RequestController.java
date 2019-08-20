package ru.hh.httpemulator.server.resources;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
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
  public Response processGet(@PathParam("any") List<PathSegment> segments,
                             @Nullable MultivaluedMap<String, String> formParameters,
                             @Context HttpServletRequest request)
      throws AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    Response.ResponseBuilder response = Response.status(Response.Status.OK);
    process(request, response, formParameters);
    return response.build();
  }

  @POST
  @Path("{any: .*}")
  public Response processPost(@PathParam("any") List<PathSegment> segments,
                              @Nullable MultivaluedMap<String, String> formParameters,
                              @Context HttpServletRequest request)
      throws AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    Response.ResponseBuilder response = Response.status(Response.Status.OK);
    process(request, response, formParameters);
    return response.build();
  }

  @PUT
  @Path("{any: .*}")
  public Response processPut(@PathParam("any") List<PathSegment> segments,
                             @Nullable MultivaluedMap<String, String> formParameters,
                             @Context HttpServletRequest request)
      throws AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    Response.ResponseBuilder response = Response.status(Response.Status.OK);
    process(request, response, formParameters);
    return response.build();
  }

  @DELETE
  @Path("{any: .*}")
  public Response processDelete(@PathParam("any") List<PathSegment> segments,
                                @Nullable MultivaluedMap<String, String> formParameters,
                                @Context HttpServletRequest request)
      throws AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    Response.ResponseBuilder response = Response.status(Response.Status.OK);
    process(request, response, formParameters);
    return response.build();
  }

  @HEAD
  @Path("{any: .*}")
  public Response processHead(@PathParam("any") List<PathSegment> segments,
                              @Nullable MultivaluedMap<String, String> formParameters,
                              @Context HttpServletRequest request)
      throws AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    Response.ResponseBuilder response = Response.status(Response.Status.OK);
    process(request, response, formParameters);
    return response.build();
  }

  @OPTIONS
  @Path("{any: .*}")
  public Response processOptions(@PathParam("any") List<PathSegment> segments,
                                 @Nullable MultivaluedMap<String, String> formParameters,
                                 @Context HttpServletRequest request)
      throws AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    Response.ResponseBuilder response = Response.status(Response.Status.OK);
    process(request, response, formParameters);
    return response.build();
  }

  @PATCH
  @Path("{any: .*}")
  public Response processPatch(@PathParam("any") List<PathSegment> segments,
                               @Nullable MultivaluedMap<String, String> formParameters,
                               @Context HttpServletRequest request)
      throws AmbiguousRulesException, ScenarioNotFoundException, RuleNotFoundException {
    Response.ResponseBuilder response = Response.status(Response.Status.OK);
    process(request, response, formParameters);
    return response.build();
  }

  private void process(HttpServletRequest request, Response.ResponseBuilder response, Map<String, List<String>> formParameters)
      throws AmbiguousRulesException, RuleNotFoundException, ScenarioNotFoundException {
    final long requestId = getRequestId(request);

    final Collection<HttpEntry> requestEntries = HttpUtils.convertToHttpEntries(request, formParameters);
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
                                     Response.ResponseBuilder response,
                                     Collection<HttpEntry> entries) throws ScenarioNotFoundException {
    for (HttpEntry httpEntry : entries) {
      switch (httpEntry.getType()) {
        case BODY:
          response.entity(httpEntry.getValue());
          break;
        case HEADER:
          response.header(httpEntry.getKey(), httpEntry.getValue());
          break;
        case COOKIE:
          response.cookie(new NewCookie(new Cookie(httpEntry.getKey(), httpEntry.getValue())));
          break;
        case STATUS:
          response.status(Integer.parseInt(httpEntry.getValue()));
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
