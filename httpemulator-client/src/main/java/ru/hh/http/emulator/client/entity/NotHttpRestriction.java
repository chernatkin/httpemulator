package ru.hh.http.emulator.client.entity;

import java.util.Collection;
import java.util.Collections;

public class NotHttpRestriction extends HttpRestriction {

	public NotHttpRestriction() {
	}
	
	public NotHttpRestriction(final HttpRestriction restriction) {
		restriction.setParent(this);
		setChilds(Collections.singleton(restriction));
	}

	@Override
	public boolean match(Collection<HttpEntry> request) {
		return !getChilds().iterator().next().match(request);
	}

	@Override
	protected boolean matchValue(String value) {
		throw new UnsupportedOperationException();
	}

}
