package org.drools.testing.core.exception;

public class CouldNotParseDrlException extends RuleTestLanguageException {

public CouldNotParseDrlException () {
		
		super();
	
	}
	
	public CouldNotParseDrlException (String message) {
	
		super(message);
	
	}
	
	public CouldNotParseDrlException (String message, Throwable cause) {
	
		super(message,cause);
	
	}
}
