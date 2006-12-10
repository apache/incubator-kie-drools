package org.codehaus.jfdi.interpreter;

public class NotAnLValueException extends RuntimeException {
	
	private Object obj;

	public NotAnLValueException(Object obj) {
		this.obj = obj;
	}
	
	public String getMessage() {
		return "Not an l-value: " + obj;
	}

}
