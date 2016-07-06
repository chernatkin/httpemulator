package ru.hh.httpemulator.server.engine;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import com.google.common.cache.CacheBuilder;
import java.util.function.Function;
import javax.inject.Named;
import javax.inject.Singleton;

import ru.hh.httpemulator.client.entity.EQHttpRestriction;
import ru.hh.httpemulator.client.entity.HttpCriteria;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.exception.AmbiguousRulesException;
import ru.hh.httpemulator.server.exception.RuleNotFoundException;

@Named
@Singleton
public class CriteriaHttpEngine implements HttpEngine {

  private final AtomicLong sequence = new AtomicLong();

  private static final long EXPIRATION_TIMEOUT = 36000;

  private Map<HttpCriteria, Collection<HttpEntry>> rulesMap;

  private final Map<Long, WeakReference<HttpCriteria>> criteriaIndex = new HashMap<>();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  @PostConstruct
  public void postConstruct() {
    rulesMap = CacheBuilder.newBuilder()
        .expireAfterAccess(EXPIRATION_TIMEOUT, TimeUnit.SECONDS)
        .concurrencyLevel(4)
        .<HttpCriteria, Collection<HttpEntry>>build()
        .asMap();
  }

  @Override
  public Collection<HttpEntry> process(Collection<HttpEntry> request) throws AmbiguousRulesException, RuleNotFoundException {

    final Lock readLock = lock.readLock();
    readLock.lock();

    try {
      return findResponse(request);
    } finally {
      readLock.unlock();
    }
  }

  private Collection<HttpEntry> findResponse(Collection<HttpEntry> request) throws AmbiguousRulesException, RuleNotFoundException {

    Map.Entry<HttpCriteria, Collection<HttpEntry>> result = null;
    for (Map.Entry<HttpCriteria, Collection<HttpEntry>> entry : rulesMap.entrySet()) {
      if (entry.getKey().match(request)) {
        if (result == null) {
          result = entry;
        } else {
          throw new AmbiguousRulesException("Rules conflict. Request:" + request
              + "\nmatch two rules:\n" + result
              + "\n and:" + entry);
        }
      }
    }

    if (result == null) {
      throw new RuleNotFoundException("Rule not found for request:" + request);
    }

    return result.getValue();
  }

  @Override
  public Long addRule(HttpEntry criteria, Collection<HttpEntry> response) throws AmbiguousRulesException {
    return addRule(new HttpCriteria()
        .addRestriction(new EQHttpRestriction(criteria.getKey(), criteria.getValue(), criteria.getType())),
        response);
  }

  @Override
  public Long addRule(HttpCriteria criteria, Collection<HttpEntry> response) throws AmbiguousRulesException {

    final long id = executeWithWriteLock(voidInput -> {
      if (rulesMap.containsKey(criteria)) {
        return -1L;
      }

      criteria.setId(sequence.incrementAndGet());
      rulesMap.put(criteria, response);
      return criteria.getId();
    });

    if (id < 0) {
      throw new AmbiguousRulesException("Rule " + criteria + " conflict with another:" + rulesMap.get(criteria));
    }

    criteriaIndex.put(id, new WeakReference<>(criteria));
    return id;
  }

  @Override
  public void deleteRule(Long id) throws RuleNotFoundException {
    if (id == null) {
      throw new RuleNotFoundException("Rule with id='null' not found");
    }

    final WeakReference<HttpCriteria> ref = criteriaIndex.remove(id);
    final HttpCriteria criteria;
    if (ref != null) {
      criteria = ref.get();
    } else {
      criteria = null;
    }

    if (criteria == null) {
      throw new RuleNotFoundException("Rule with id='" + id + "' not found");
    }

    executeWithWriteLock(voidInput -> rulesMap.remove(criteria));
  }

  @Override
  public void deleteAll() {
    executeWithWriteLock(voidInput -> {
      rulesMap.clear();
      return null;
    });

    criteriaIndex.clear();
  }

  private <V> V executeWithWriteLock(Function<Void, V> func) {
    final Lock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      return func.apply(null);
    } finally {
      writeLock.unlock();
    }
  }
}
