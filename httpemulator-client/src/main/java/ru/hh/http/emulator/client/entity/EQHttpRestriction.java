package ru.hh.http.emulator.client.entity;

public class EQHttpRestriction extends HttpRestriction {
  public EQHttpRestriction() {
    super();
  }

  public EQHttpRestriction(String key, String value, AttributeType attribyteType) {
    super(RestrictionType.EQ, key, value, attribyteType);
  }

  @Override
  protected boolean matchValue(final String value) {
    return getValue() == value || (getValue() != null && getValue().equals(value));
  }
}
