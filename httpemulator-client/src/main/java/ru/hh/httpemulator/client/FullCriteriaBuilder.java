package ru.hh.httpemulator.client;

import org.eclipse.jetty.client.api.ContentResponse;

import ru.hh.httpemulator.client.entity.AttributeType;
import ru.hh.httpemulator.client.entity.EQHttpRestriction;
import ru.hh.httpemulator.client.entity.HttpCriteria;
import ru.hh.httpemulator.client.entity.HttpRestriction;

public class FullCriteriaBuilder extends CriteriaBuilder<FullCriteriaBuilder> {

  private HttpCriteria criteria;

  public FullCriteriaBuilder(EmulatorClient client) {
    super(client);
  }

  @Override
  public FullCriteriaBuilder addEQ(AttributeType type, String key, String value) {
    getCriteria().addRestriction(new EQHttpRestriction(key, value, type));
    return this;
  }

  @Override
  public FullCriteriaBuilder add(HttpRestriction restriction) {
    getCriteria().addRestriction(restriction);
    return this;
  }

  @Override
  protected ContentResponse sendRequest() throws Exception {
    return getClient().putRule(criteria, getResult());
  }

  @Override
  protected FullCriteriaBuilder self() {
    return this;
  }

  protected HttpCriteria getCriteria() {
    if (criteria == null) {
      criteria = new HttpCriteria();
    }

    return criteria;
  }
}
