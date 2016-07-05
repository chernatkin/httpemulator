package ru.hh.http.emulator.scenario;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import ru.hh.http.emulator.RequestController;
import ru.hh.http.emulator.client.entity.AttributeType;
import ru.hh.http.emulator.client.entity.HttpEntry;

public class TimeoutScenario implements Scenario {

	private final Logger LOGGER = LoggerFactory.getLogger(TimeoutScenario.class);
	
	private static final int DEFAULT_TIMEOUT = 5000;
	
	private static final String TIMEOUT_KEY = "timeout";
	
	@Override
	public Collection<HttpEntry> execute(HttpServletRequest request, HttpServletResponse response, Collection<HttpEntry> otherEntries) {
		
		int timeout = DEFAULT_TIMEOUT; 
		if(!CollectionUtils.isEmpty(otherEntries)){
			for (HttpEntry httpEntry : otherEntries) {
				if(httpEntry.getType().equals(AttributeType.PARAMETER) && TIMEOUT_KEY.equals(httpEntry.getKey())){
					timeout = Integer.parseInt(httpEntry.getValue());
				}
			}
		}
		
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			LOGGER.warn("", e);
		}
		
		return otherEntries;
	}

}
