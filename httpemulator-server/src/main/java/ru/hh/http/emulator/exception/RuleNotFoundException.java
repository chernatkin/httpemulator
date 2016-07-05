package ru.hh.http.emulator.exception;

public class RuleNotFoundException extends Exception {

	public RuleNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuleNotFoundException(String message) {
		super(message);
	}
	
}
