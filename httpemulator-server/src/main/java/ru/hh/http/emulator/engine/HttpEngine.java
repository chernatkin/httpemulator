package ru.hh.http.emulator.engine;

import java.util.Collection;

import ru.hh.http.emulator.client.entity.HttpCriteria;
import ru.hh.http.emulator.client.entity.HttpEntry;
import ru.hh.http.emulator.exception.AmbiguousRulesException;
import ru.hh.http.emulator.exception.RuleNotFoundException;

public interface HttpEngine {

	public Collection<HttpEntry> process(Collection<HttpEntry> request) throws AmbiguousRulesException, RuleNotFoundException;
	
	public Long addRule(HttpEntry criteria, Collection<HttpEntry> response) throws AmbiguousRulesException;
	
	public Long addRule(HttpCriteria criteria, Collection<HttpEntry> response) throws AmbiguousRulesException;
	
	public void deleteRule(Long id) throws RuleNotFoundException;
	
	public void deleteAll();
}
