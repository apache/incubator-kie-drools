package org.drools.analytics.result;

import org.drools.analytics.components.Field;
import org.drools.analytics.components.LiteralRestriction;

/**
 * 
 * @author Toni Rikkola
 */
public class Gap extends MissingRange implements RangeCheckCause, Comparable {

	private LiteralRestriction restriction;

	/**
	 * Takes the given evaluator e, and returns a reversed version of it.
	 * 
	 * @return evaluator
	 */
	public static String getReversedEvaluator(String e) {
		if (e.equals("!=")) {
			return "==";
		} else if (e.equals("==")) {
			return "!=";
		} else if (e.equals(">")) {
			return "<=";
		} else if (e.equals("<")) {
			return ">=";
		} else if (e.equals(">=")) {
			return "<";
		} else if (e.equals("<=")) {
			return ">";
		}

		return e;
	}

	public int compareTo(Object another) {
		return super.compareTo(another);
	}

	public CauseType getCauseType() {
		return Cause.CauseType.GAP;
	}

	/**
	 * 
	 * @param field
	 *            Field from where the value is missing.
	 * @param evaluator
	 *            Evaluator for the missing value.
	 * @param cause
	 *            The restriction that the gap begins from.
	 */
	public Gap(Field field, String evaluator, LiteralRestriction restriction) {
		this.field = field;
		this.evaluator = evaluator;
		this.restriction = restriction;
	}

	public String getRuleName() {
		return restriction.getRuleName();
	}

	public LiteralRestriction getRestriction() {
		return restriction;
	}

	public void setRestriction(LiteralRestriction restriction) {
		this.restriction = restriction;
	}

	public String getValueAsString() {
		return restriction.getValueAsString();
	}

	public Object getValueAsObject() {
		return restriction.getValueAsObject();
	}

	@Override
	public String toString() {
		return "Gap: (" + field + ") " + getEvaluator() + " "
				+ getValueAsString();
	}
}
