package ru.hh.httpemulator.client.entity;

public class HttpEntry {
  private Long id;

  // private HttpRestriction restriction;

  private AttributeType type;

  private String key;

  private String value;

  public HttpEntry() { }

  public HttpEntry(AttributeType type, String key, String value) {
    this.type = type;
    this.key = key;
    this.value = value;
  }

  public AttributeType getType() {
    return type;
  }

  public void setType(AttributeType type) {
    this.type = type;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /*public HttpRestriction getRestriction() {
    return restriction;
  }

  public void setRestriction(HttpRestriction restriction) {
    this.restriction = restriction;
  }*/

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    HttpEntry other = (HttpEntry) obj;
    final String otherKey = other.getKey();
    if (key == null) {
      if (otherKey != null) {
        return false;
      }
    } else if (!key.equals(otherKey)) {
      return false;
    }
    if (type != other.getType()) {
      return false;
    }
    final String otherValue = other.getValue();
    if (value == null) {
      if (otherValue != null) {
        return false;
      }
    } else if (!value.equals(otherValue)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "HttpEntry [id=" + id + ", type=" + type + ", " + key
    + ": " + value + "]";
  }
}
