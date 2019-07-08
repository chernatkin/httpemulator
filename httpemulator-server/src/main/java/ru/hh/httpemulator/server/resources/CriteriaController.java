package ru.hh.httpemulator.server.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import java.io.IOException;
import java.util.Collection;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import ru.hh.httpemulator.client.entity.HttpCriteria;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.engine.CriteriaHttpEngine;
import ru.hh.httpemulator.server.utils.exception.AmbiguousRulesException;
import ru.hh.httpemulator.server.utils.exception.RuleNotFoundException;

@Path("/criteria")
public class CriteriaController {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final CriteriaHttpEngine engine;

  @Inject
  public CriteriaController(CriteriaHttpEngine engine) {
    this.engine = engine;
    MAPPER.registerModule(new Hibernate5Module());
  }

  @PUT
  @Path("/simple")
  @Produces({MediaType.TEXT_PLAIN})
  public String createSimpleCriteria(@QueryParam("rule") String httpEntry,
                                     @QueryParam("response") String response) throws IOException, AmbiguousRulesException {
    return engine.addRule(
        MAPPER.readValue(httpEntry, HttpEntry.class),
        MAPPER.readValue(response, MAPPER.getTypeFactory().constructCollectionType(Collection.class, HttpEntry.class))
    ).toString();
  }

  @PUT
  @Path("/full")
  @Produces({MediaType.TEXT_PLAIN})
  public String createCriteria(@QueryParam("criteria") String criteria,
                               @QueryParam("response") String response) throws IOException, AmbiguousRulesException {
    return engine.addRule(
        MAPPER.readValue(criteria, HttpCriteria.class),
        MAPPER.readValue(response, MAPPER.getTypeFactory().constructCollectionType(Collection.class, HttpEntry.class))
    ).toString();
  }

  @DELETE
  @Path("{id:[0-9]+}")
  public void deleteCriteria(@PathParam("id") Long id) throws RuleNotFoundException {
    engine.deleteRule(id);
  }

  @DELETE
  @Path("/all")
  public void deleteAllCriteria() {
    engine.deleteAll();
  }
}
