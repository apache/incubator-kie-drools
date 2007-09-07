package org.drools.analytics.result;

import org.drools.analytics.components.Field;

/**
 * 
 * @author Toni Rikkola
 */
public class Gap implements Cause {

	private static int index = 0;

	private int id = index++;

	private Field field;
	private RangeCheckCause cause;
	private String firedRuleName;

	public Gap(Field field, RangeCheckCause cause, String firedRuleName) {
		this.field = field;
		this.cause = cause;
		this.firedRuleName = firedRuleName;
	}

	public int getId() {
		return id;
	}

	public String getRuleName() {
		return cause.getRuleName();
	}

	/**
	 * Evaluator is reversed unless the cause is MissingNumberPattern. For
	 * others the evaluator needs to be turned in order to show the missing
	 * range, not the the range that cause covers.
	 * 
	 * @return evaluator
	 */
	private String getCauseEvaluator() {
		if (cause instanceof MissingNumberPattern) {
			return cause.getEvaluator();
		} else {
			if (cause.getEvaluator().equals("!=")) {
				return "==";
			} else if (cause.getEvaluator().equals("==")) {
				return "!=";
			} else if (cause.getEvaluator().equals(">")) {
				return "<=";
			} else if (cause.getEvaluator().equals("<")) {
				return ">=";
			} else if (cause.getEvaluator().equals(">=")) {
				return "<";
			} else if (cause.getEvaluator().equals("<=")) {
				return ">";
			}
		}

		return null;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public RangeCheckCause getRangeCheckCause() {
		return cause;
	}

	public void setRangeCheckCause(RangeCheckCause cause) {
		this.cause = cause;
	}

	public String getFiredRuleName() {
		return firedRuleName;
	}

	public void setFiredRuleName(String firedRuleName) {
		this.firedRuleName = firedRuleName;
	}

	@Override
	public String toString() {
		return "Gap: (" + field + ") " + getCauseEvaluator() + " "
				+ cause.getValueAsString();
	}
}
