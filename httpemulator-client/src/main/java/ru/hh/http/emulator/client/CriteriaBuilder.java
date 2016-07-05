package ru.hh.http.emulator.client;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jetty.client.api.ContentResponse;

import ru.hh.http.emulator.client.entity.AttributeType;
import ru.hh.http.emulator.client.entity.HttpEntry;
import ru.hh.http.emulator.client.entity.HttpRestriction;

public abstract class CriteriaBuilder<T> { 
	
	private final Collection<HttpEntry> result = new ArrayList<HttpEntry>();

	private final EmulatorClient client;
	
	public CriteriaBuilder(EmulatorClient client) {
		this.client = client;
	}

	public abstract T addEQ(final AttributeType type, final String key, final String value);

	public abstract T add(final HttpRestriction restriction);
		  
	public T addResponseEntry(final AttributeType type, final String key, final String value) {
	    getResult().add(new HttpEntry(type, key, value));
	    return self();
	}
	
	public long save() throws Exception{
	    final ContentResponse response = sendRequest();
	    if (response.getStatus() == 200) {
	      return Long.parseLong(response.getContentAsString());
	    }
	    
	    throw new IllegalStateException("HTTP status code = " + response.getStatus());
	}

	protected abstract ContentResponse sendRequest() throws Exception;
	
	protected abstract T self();
	
	protected Collection<HttpEntry> getResult() {
		return result;
	}

	protected EmulatorClient getClient() {
		return client;
	}
}