package ru.hh.httpemulator.server.utils.exception;

public class RuleNotFoundException extends Exception {

  public RuleNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public RuleNotFoundException(String message) {
    super(message);
  }

}
