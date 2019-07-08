package ru.hh.httpemulator.server.engine;

import java.util.Collection;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.utils.exception.ScenarioNotFoundException;
import ru.hh.httpemulator.server.scenario.Scenario;

public class ScenarioEngine {

  @Inject
  private Map<String, Scenario> scenarios;

  public Collection<HttpEntry> executeScenario(String scenarioName, HttpServletRequest request, HttpServletResponse response,
      Collection<HttpEntry> otherEntries) throws ScenarioNotFoundException {
    final Scenario scenario = scenarios.get(scenarioName);
    if (scenario == null) {
      throw new ScenarioNotFoundException("Scenario {" + scenarioName + "} not found");
    }

    return scenario.execute(request, response, otherEntries);
  }
}
