package org.drools.analytics.result;

import java.io.Serializable;

/**
 * 
 * @author Toni Rikkola
 */
abstract class AnalysisMessage implements Serializable {

	protected int id;

	protected String ruleName;
	protected String message;
	protected int lineNumber;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String toString() {
		return ruleName + ": " + message + " On line " + lineNumber;
	}
}
