package ru.hh.http.emulator.client;

import org.eclipse.jetty.client.api.ContentResponse;

import ru.hh.http.emulator.client.entity.AttributeType;
import ru.hh.http.emulator.client.entity.EQHttpRestriction;
import ru.hh.http.emulator.client.entity.HttpCriteria;
import ru.hh.http.emulator.client.entity.HttpEntry;
import ru.hh.http.emulator.client.entity.HttpRestriction;

public class FullCriteriaBuilder extends CriteriaBuilder<FullCriteriaBuilder> {
	
	  private HttpCriteria criteria;

	  public FullCriteriaBuilder(final EmulatorClient client) {
	    super(client);
	  }

	  @Override
	  public FullCriteriaBuilder addEQ(final AttributeType type, final String key, final String value) {
	    getCriteria().addRestriction(new EQHttpRestriction(key, value, type));
	    return this;
	  }

	  @Override
	  public FullCriteriaBuilder add(final HttpRestriction restriction) {
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

	  protected HttpCriteria getCriteria(){
		  if(criteria == null){
			  criteria = new HttpCriteria();
		  }
		  
		  return criteria;
	  }
}
