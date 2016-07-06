package ru.hh.httpemulator.server.engine;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Named;
import javax.inject.Singleton;

import ru.hh.httpemulator.client.entity.HttpCriteria;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.exception.AmbiguousRulesException;
import ru.hh.httpemulator.server.exception.RuleNotFoundException;

@Named
@Singleton
public class SimpleHttpEngine implements HttpEngine {

  private final AtomicLong sequence = new AtomicLong();

  private final Map<HttpEntry, Collection<HttpEntry>> rules = new ConcurrentHashMap<>();

  @Override
  public Collection<HttpEntry> process(Collection<HttpEntry> request) throws AmbiguousRulesException, RuleNotFoundException {

    Collection<HttpEntry> response = null;
    HttpEntry responseRule = null;
    for (HttpEntry httpEntry : request) {
      final Collection<HttpEntry> responseCandidate = rules.get(httpEntry);
      if (responseCandidate != null) {
        if (response == null) {
          response = responseCandidate;
          responseRule = httpEntry;
        } else {
          throw new AmbiguousRulesException("Rules conflict. Request:" + request
              + "\nmatch two rules:\n" + responseRule
              + "\n and:" + httpEntry);
        }
      }
    }

    if (response != null) {
      return response;
    } else {
      throw new RuleNotFoundException("Rule not found for request:" + request);
    }
  }

  @Override
  public Long addRule(HttpEntry rule, Collection<HttpEntry> response) throws AmbiguousRulesException {
    if (rules.containsKey(rule)) {
      throw new AmbiguousRulesException("Rule " + rule + " conflict with:" + rules.get(rule));
    }

    rule.setId(sequence.incrementAndGet());
    rules.put(rule, response);

    return rule.getId();
  }

  @Override
  public Long addRule(HttpCriteria criteria, Collection<HttpEntry> response) throws AmbiguousRulesException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteRule(Long id) throws RuleNotFoundException {
    if (id == null) {
      throw new RuleNotFoundException("Rule with id='null' not found");
    }

    for (Iterator<HttpEntry> it = rules.keySet().iterator(); it.hasNext();) {
      if (it.next().getId().equals(id)) {
        it.remove();
        return;
      }
    }

    throw new RuleNotFoundException("Rule with id='" + id + "' not found");
  }

  @Override
  public void deleteAll() {
    rules.clear();
  }

}
