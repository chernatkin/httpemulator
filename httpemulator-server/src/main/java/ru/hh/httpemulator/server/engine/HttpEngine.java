package ru.hh.httpemulator.server.engine;

import java.util.Collection;

import ru.hh.httpemulator.client.entity.HttpCriteria;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.exception.AmbiguousRulesException;
import ru.hh.httpemulator.server.exception.RuleNotFoundException;

public interface HttpEngine {

  Collection<HttpEntry> process(Collection<HttpEntry> request) throws AmbiguousRulesException, RuleNotFoundException;

  Long addRule(HttpEntry criteria, Collection<HttpEntry> response) throws AmbiguousRulesException;

  Long addRule(HttpCriteria criteria, Collection<HttpEntry> response) throws AmbiguousRulesException;

  void deleteRule(Long id) throws RuleNotFoundException;

  void deleteAll();
}
