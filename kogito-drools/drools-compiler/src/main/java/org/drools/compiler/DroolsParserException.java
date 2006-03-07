package org.drools.compiler;

import org.drools.CheckedDroolsException;

public class DroolsParserException extends CheckedDroolsException {
	/**
	 * @see java.lang.Exception#Exception()
	 */
	public DroolsParserException() {
		super();
	}

	/**
	 * @see java.lang.Exception#Exception(String message)
	 */
	public DroolsParserException(String message) {
		super(message);
	}

	/**
	 * @see java.lang.Exception#Exception(String message, Throwable cause)
	 */
	public DroolsParserException(String message, Throwable cause) {
		super(message);
	}

	/**
	 * @see java.lang.Exception#Exception(Throwable cause)
	 */
	public DroolsParserException(Throwable cause) {
		super(cause);
	}

}
