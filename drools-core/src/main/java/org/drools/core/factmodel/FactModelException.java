package org.drools.core.factmodel;

public class FactModelException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FactModelException(String message) {
		super(message);
	}

	public FactModelException(String message, Throwable cause) {
		super(message, cause);
	}

	public FactModelException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
