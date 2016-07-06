package ru.hh.httpemulator.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ru.hh.httpemulator.client.entity.HttpCriteria;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.engine.CriteriaHttpEngine;
import ru.hh.httpemulator.server.exception.AmbiguousRulesException;
import ru.hh.httpemulator.server.exception.RuleNotFoundException;

@Controller
@Order(Ordered.LOWEST_PRECEDENCE)
@RequestMapping("criteria")
public class CriteriaController {
  private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaController.class);

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Inject
  private CriteriaHttpEngine engine;

  @PostConstruct
  public void postConstruct() {
    MAPPER.registerModule(new Hibernate5Module());
  }

  @RequestMapping(value = "simple", method = RequestMethod.PUT, produces = { "text/plain" })
  @ResponseBody
  public String createSimpleCriteria(@RequestParam("rule") String httpEntry, @RequestParam("response") String response)
      throws JsonParseException, JsonMappingException, IOException, AmbiguousRulesException {
    return engine.addRule(
        MAPPER.readValue(httpEntry, HttpEntry.class),
        MAPPER.readValue(response, MAPPER.getTypeFactory().constructCollectionType(Collection.class, HttpEntry.class))
    ).toString();
  }
  
  @RequestMapping(value = "full", method = RequestMethod.PUT, produces = { "text/plain" })
  @ResponseBody
  public String createCriteria(@RequestParam("criteria") String criteria, @RequestParam("response") String response)
      throws JsonParseException, JsonMappingException, IOException, AmbiguousRulesException {
    return engine.addRule(
        MAPPER.readValue(criteria, HttpCriteria.class),
        MAPPER.readValue(response, MAPPER.getTypeFactory().constructCollectionType(Collection.class, HttpEntry.class))
    ).toString();
  }

  @RequestMapping(value = "{id:[0-9]+}", method = RequestMethod.DELETE)
  @ResponseBody
  public void deleteCriteria(@PathVariable("id") Long id)
      throws JsonParseException, JsonMappingException, IOException, AmbiguousRulesException, RuleNotFoundException {
    engine.deleteRule(id);
  }

  @RequestMapping(value = "all", method = RequestMethod.DELETE)
  @ResponseBody
  public void deleteAllCriteria() throws JsonParseException, JsonMappingException, IOException, AmbiguousRulesException {
    engine.deleteAll();
  }

  @ExceptionHandler({ AmbiguousRulesException.class })
  @ResponseBody
  @ResponseStatus(HttpStatus.CONFLICT)
  public String badRequest(AmbiguousRulesException e) {
    LOGGER.warn(HttpStatus.CONFLICT.toString(), e);
    return e.getMessage();
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String internalFail(Exception e) {
    LOGGER.warn(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e);
    return e.getMessage();
  }
}
