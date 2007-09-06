package org.drools.analytics.result;

import org.drools.analytics.components.Restriction;

/**
 * 
 * @author Toni Rikkola
 */
public class Gap implements Cause {

	private static int index = 0;

	private int id;

	private Cause cause;
	private Restriction restriction;
	private String firedRuleName;

	public Gap(Cause cause, Restriction restriction, String firedRuleName) {
		id = index;
		this.cause = cause;
		this.restriction = restriction;
		this.firedRuleName = firedRuleName;
	}

	public int getId() {
		return id;
	}

	public String getRuleName() {
		return restriction.getRuleName();
	}

	private String getReversedEvaluator() {
		if (restriction.getEvaluator().equals("!=")) {
			return "==";
		} else if (restriction.getEvaluator().equals("==")) {
			return "!=";
		} else if (restriction.getEvaluator().equals(">")) {
			return "<=";
		} else if (restriction.getEvaluator().equals("<")) {
			return ">=";
		} else if (restriction.getEvaluator().equals(">=")) {
			return "<";
		} else if (restriction.getEvaluator().equals("<=")) {
			return ">";
		}

		return null;
	}

	public Cause getCause() {
		return cause;
	}

	public void setCause(Cause cause) {
		this.cause = cause;
	}

	public Restriction getRestriction() {
		return restriction;
	}

	public void setRestriction(Restriction restriction) {
		this.restriction = restriction;
	}

	public String getFiredRuleName() {
		return firedRuleName;
	}

	public void setFiredRuleName(String firedRuleName) {
		this.firedRuleName = firedRuleName;
	}

	@Override
	public String toString() {
		return "Gap: (" + cause + ") " + getReversedEvaluator() + " "
				+ restriction.getValueAsString();
	}
}
