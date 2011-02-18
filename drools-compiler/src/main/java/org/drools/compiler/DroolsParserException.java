/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler;

import org.drools.CheckedDroolsException;

public class DroolsParserException extends CheckedDroolsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 510l;

	private String errorCode = null;
	private int lineNumber;
	private int column;
	private int offset;

	/**
	 * @see java.lang.Exception#Exception()
	 */
	public DroolsParserException() {
		super();
	}

	/**
	 * @see java.lang.Exception#Exception(String message)
	 */
	public DroolsParserException(final String message) {
		super(message);
	}

	/**
	 * @see java.lang.Exception#Exception(String message, Throwable cause)
	 */
	public DroolsParserException(final String message, final Throwable cause) {
		super(message);
	}

	/**
	 * @see java.lang.Exception#Exception(Throwable cause)
	 */
	public DroolsParserException(final Throwable cause) {
		super(cause);
	}

	/**
	 * DroolsParserException constructor.
	 * 
	 * @param errorCode
	 *            error code
	 * @param message
	 *            message
	 * @param lineNumber
	 *            line number
	 * @param column
	 *            column
	 * @param offset
	 *            offset
	 * @param cause
	 *            exception cause
	 */
	public DroolsParserException(String errorCode, String message, int lineNumber,
			int column, int offset, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.lineNumber = lineNumber;
		this.column = column;
		this.offset = offset;
	}

	public String getMessage() {
		if (null == errorCode) {
			return super.getMessage();
		}
		return "[" + errorCode + "] " + super.getMessage();
	}

	/**
	 * getter for error code
	 * 
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * getter for line number
	 * 
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * getter for column position
	 * 
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * getter for char offset
	 * 
	 */
	public int getOffset() {
		return offset;
	}
}
