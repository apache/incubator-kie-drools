package org.drools.analytics.components;

import java.io.Serializable;

/**
 * 
 * @author Toni Rikkola
 */
public class Constraint extends AnalyticsComponent implements Serializable {
	private static final long serialVersionUID = -1564096753608593465L;

	private static int index = 0;

	private int ruleId;
	private int patternId;
	private boolean patternIsNot;
	private int fieldId;
	private int lineNumber;

	public Constraint() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.CONSTRAINT;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getPatternId() {
		return patternId;
	}

	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}

	public boolean isPatternIsNot() {
		return patternIsNot;
	}

	public void setPatternIsNot(boolean patternIsNot) {
		this.patternIsNot = patternIsNot;
	}
}
