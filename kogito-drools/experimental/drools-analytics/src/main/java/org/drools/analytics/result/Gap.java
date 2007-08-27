package org.drools.analytics.result;

/**
 * 
 * @author Toni Rikkola
 */
public class Gap {

	private Cause cause;
	private String ruleName;
	private String firedRuleName;
	private String evaluator;
	private String value;

	public String getReversedEvaluator() {
		if (evaluator.equals("!=")) {
			return "==";
		} else if (evaluator.equals("==")) {
			return "!=";
		} else if (evaluator.equals(">")) {
			return "<=";
		} else if (evaluator.equals("<")) {
			return ">=";
		} else if (evaluator.equals(">=")) {
			return "<";
		} else if (evaluator.equals("<=")) {
			return ">";
		}

		return evaluator;
	}

	public Cause getCause() {
		return cause;
	}

	public void setCause(Cause cause) {
		this.cause = cause;
	}

	public String getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}

	public String getFiredRuleName() {
		return firedRuleName;
	}

	public void setFiredRuleName(String firedRuleName) {
		this.firedRuleName = firedRuleName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Gap: (" + cause + ") " + getReversedEvaluator() + " " + value;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}
