package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public class Constraint extends AnalyticsComponent implements Cause {

	private static int index = 0;

	private int ruleId;
	private int patternId;
	private boolean patternIsNot;
	private int fieldId;
	private String fieldName;
	private int lineNumber;

	public Constraint() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.CONSTRAINT;
	}

	public CauseType getCauseType() {
		return CauseType.CONSTRAINT;
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

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String toString() {
		return "Constraint id: " + id + " field name: " + fieldName;
	}
}
