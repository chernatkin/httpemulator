package ru.hh.http.emulator.scenario;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.hh.http.emulator.client.entity.HttpEntry;

public interface Scenario {

	public Collection<HttpEntry> execute(HttpServletRequest request, HttpServletResponse response, Collection<HttpEntry> otherEntries);
	
}
