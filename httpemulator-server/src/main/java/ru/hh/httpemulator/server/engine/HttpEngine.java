package ru.hh.httpemulator.server.engine;

import java.util.Collection;

import ru.hh.httpemulator.client.entity.HttpCriteria;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.exception.AmbiguousRulesException;
import ru.hh.httpemulator.server.exception.RuleNotFoundException;

public interface HttpEngine {

	public Collection<HttpEntry> process(Collection<HttpEntry> request) throws AmbiguousRulesException, RuleNotFoundException;
	
	public Long addRule(HttpEntry criteria, Collection<HttpEntry> response) throws AmbiguousRulesException;
	
	public Long addRule(HttpCriteria criteria, Collection<HttpEntry> response) throws AmbiguousRulesException;
	
	public void deleteRule(Long id) throws RuleNotFoundException;
	
	public void deleteAll();
}
