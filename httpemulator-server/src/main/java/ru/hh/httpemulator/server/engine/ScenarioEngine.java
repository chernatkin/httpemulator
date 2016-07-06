package ru.hh.httpemulator.server.engine;

import java.util.Collection;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.exception.ScenarioNotFoundException;
import ru.hh.httpemulator.server.scenario.Scenario;

@Named
@Singleton
public class ScenarioEngine {

  @Inject
  private Map<String, Scenario> scenaries;

  public Collection<HttpEntry> executeScenario(String scenarioName, HttpServletRequest request, HttpServletResponse response,
      Collection<HttpEntry> otherEntries) throws ScenarioNotFoundException {
    final Scenario scenario = scenaries.get(scenarioName);
    if (scenario == null) {
      throw new ScenarioNotFoundException("Scenario {" + scenarioName + "} not found");
    }

    return scenario.execute(request, response, otherEntries);
  }
}
