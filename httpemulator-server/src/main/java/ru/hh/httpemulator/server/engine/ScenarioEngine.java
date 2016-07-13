package ru.hh.httpemulator.server.engine;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.exception.ScenarioNotFoundException;
import ru.hh.httpemulator.server.scenario.Scenario;

@Component
public class ScenarioEngine {

	private Map<String, Scenario> scenaries;
	
	public Collection<HttpEntry> executeScenario(final String scenarionName, final HttpServletRequest request, final HttpServletResponse response, final Collection<HttpEntry> otherEntries) throws ScenarioNotFoundException{
		final Scenario scenario = scenaries.get(scenarionName);
		if(scenario == null){
			throw new ScenarioNotFoundException("Scenario {" + scenarionName + "} not found");
		}
		
		return scenario.execute(request, response, otherEntries);
	}

	public Map<String, Scenario> getScenaries() {
		return scenaries;
	}

	public void setScenaries(Map<String, Scenario> scenaries) {
		this.scenaries = scenaries;
	}
	
	
}
