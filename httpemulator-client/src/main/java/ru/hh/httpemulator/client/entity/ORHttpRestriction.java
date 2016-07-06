package ru.hh.httpemulator.client.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ORHttpRestriction extends HttpRestriction {

  public ORHttpRestriction() {
  }

  public ORHttpRestriction(HttpRestriction... restrictions) {
    final Set<HttpRestriction> childs = new HashSet<>(restrictions.length);
    for (HttpRestriction httpRestriction : restrictions) {
      childs.add(httpRestriction);
      httpRestriction.setParent(this);
    }

    setChilds(childs);
  }

  @Override
  public boolean match(Collection<HttpEntry> request) {

    for (HttpRestriction child : getChilds()) {
      if (child.match(request)) {
        return true;
      }
    }

    return false;
  }

  @Override
  protected boolean matchValue(String value) {
    throw new UnsupportedOperationException();
  }

}
