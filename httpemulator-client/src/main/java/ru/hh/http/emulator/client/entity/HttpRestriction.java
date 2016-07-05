package ru.hh.http.emulator.client.entity;

import java.util.Collection;
import java.util.Set;

public abstract class HttpRestriction {
  private Long id;

  private RestrictionType restrictionType;

  private String key;

  private String value;

  private AttributeType attribyteType;

  private HttpRestriction parent;

  private Set<HttpRestriction> childs;

  public HttpRestriction() { }

  public HttpRestriction(RestrictionType restrictionType, String key, String value, AttributeType attribyteType) {
    this.restrictionType = restrictionType;
    this.key = key;
    this.value = value;
    this.attribyteType = attribyteType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public RestrictionType getRestrictionType() {
    return restrictionType;
  }

  public void setRestrictionType(RestrictionType restrictionType) {
    this.restrictionType = restrictionType;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public AttributeType getAttribyteType() {
    return attribyteType;
  }

  public void setAttribyteType(AttributeType attribyteType) {
    this.attribyteType = attribyteType;
  }

  public HttpRestriction getParent() {
    return parent;
  }

  public void setParent(HttpRestriction parent) {
    this.parent = parent;
  }

  public Set<HttpRestriction> getChilds() {
    return childs;
  }

  public void setChilds(Set<HttpRestriction> childs) {
    this.childs = childs;
  }

  protected abstract boolean matchValue(final String value);

  public boolean match(final Collection<HttpEntry> request) {
    for (HttpEntry httpEntry : request) {
      if (httpEntry.getType().equals(attribyteType) && (httpEntry.getKey() == key || (key != null && key.equals(httpEntry.getKey())))) {
        return matchValue(httpEntry.getValue());
      }
    }

    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((attribyteType == null) ? 0 : attribyteType.hashCode());
    result = prime * result + ((childs == null) ? 0 : childs.hashCode());
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((restrictionType == null) ? 0 : restrictionType.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    HttpRestriction other = (HttpRestriction) obj;
    if (attribyteType != other.attribyteType) {
      return false;
    }
    if (childs == null) {
      if (other.childs != null) {
        return false;
      }
    } else if (!childs.equals(other.childs)) {
      return false;
    }
    if (key == null) {
      if (other.key != null) {
        return false;
      }
    } else if (!key.equals(other.key)) {
      return false;
    }
    if (restrictionType != other.restrictionType) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }
}
