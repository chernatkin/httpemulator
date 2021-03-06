package ru.hh.httpemulator.client.entity;

import java.util.ArrayList;
import java.util.Collection;

public class HttpCriteria {
  private Long id;

  private Collection<HttpRestriction> restrictions;

  public Collection<HttpRestriction> getRestrictions() {
    if (restrictions == null) {
      restrictions = new ArrayList<>();
    }
    return restrictions;
  }

  public void setRestrictions(Collection<HttpRestriction> restrictions) {
    this.restrictions = restrictions;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean match(Collection<HttpEntry> request) {
    if (restrictions == null) {
      return false;
    }

    for (HttpRestriction restriction : restrictions) {
      if (!restriction.match(request)) {
        return false;
      }
    }

    return true;
  }

  public HttpCriteria addRestriction(HttpRestriction restriction) {
    getRestrictions().add(restriction);
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((restrictions == null) ? 0 : restrictions.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    HttpCriteria other = (HttpCriteria) obj;
    final Collection<HttpRestriction> otherRestrictions = other.getRestrictions();
    if (restrictions == null) {
      if (otherRestrictions != null) {
        return false;
      }
    } else if (!restrictions.equals(otherRestrictions)) {
      return false;
    }
    return true;
  }
}
