package org.codehaus.jfdi.interpreter;

public class CoercionException extends RuntimeException {
	
	private Object obj;
	private String type;

	public CoercionException(Object obj, String type) {
		this.obj = obj;
		this.type = type;
	}
	
	public String getMessage() {
		return "Invalid coercion: " + obj.getClass().getName() + " to " + type;
	}

}
