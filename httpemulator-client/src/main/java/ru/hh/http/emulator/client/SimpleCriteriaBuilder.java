package ru.hh.http.emulator.client;

import org.eclipse.jetty.client.api.ContentResponse;

import ru.hh.http.emulator.client.entity.AttributeType;
import ru.hh.http.emulator.client.entity.HttpEntry;
import ru.hh.http.emulator.client.entity.HttpRestriction;

public class SimpleCriteriaBuilder extends CriteriaBuilder<SimpleCriteriaBuilder> {
	
  private HttpEntry rule;

  public SimpleCriteriaBuilder(final EmulatorClient client) {
    super(client);
  }

  @Override
  public SimpleCriteriaBuilder addEQ(final AttributeType type, final String key, final String value) {
	if(rule != null){
		throw new IllegalStateException("Simple builder allow only one restriction");
	}
    rule = new HttpEntry(type, key, value);
    return this;
  }

  @Override
  public SimpleCriteriaBuilder add(final HttpRestriction restriction) {
	rule = new HttpEntry(restriction.getAttribyteType(), restriction.getKey(), restriction.getValue());
	return this;
  }
  
  @Override
  protected ContentResponse sendRequest() throws Exception {
	return getClient().putSimple(rule, getResult());
  }

  @Override
  protected SimpleCriteriaBuilder self() {
	return this;
  }
}
