package org.drools.analytics.components;

import org.drools.analytics.report.components.Cause;

/**
 * 
 * @author Toni Rikkola
 */
public abstract class Restriction extends AnalyticsComponent implements Cause {
	public static enum RestrictionType {
		LITERAL, VARIABLE, QUALIFIED_IDENTIFIER, RETURN_VALUE_RESTRICTION
	}

	private static int index = 0;

	private int ruleId;
	private int patternId;
	private boolean patternIsNot;
	private int constraintId;
	// Id of the field that this restriction is compared to.
	private int fieldId;

	private String evaluator;

	public Restriction() {
		super(index++);
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.RESTRICTION;
	}

	public CauseType getCauseType() {
		return Cause.CauseType.RESTRICTION;
	}

	public abstract RestrictionType getRestrictionType();

	public String getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}

	public int getConstraintId() {
		return constraintId;
	}

	public void setConstraintId(int constraintId) {
		this.constraintId = constraintId;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public int getPatternId() {
		return patternId;
	}

	public void setPatternId(int patternId) {
		this.patternId = patternId;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public boolean isPatternIsNot() {
		return patternIsNot;
	}

	public void setPatternIsNot(boolean patternIsNot) {
		this.patternIsNot = patternIsNot;
	}
}
