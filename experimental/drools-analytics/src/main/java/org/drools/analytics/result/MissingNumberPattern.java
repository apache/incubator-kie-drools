package org.drools.analytics.result;

import org.drools.analytics.components.Field;

/**
 * 
 * @author Toni Rikkola
 */
public class MissingNumberPattern extends MissingRange implements
		RangeCheckCause, Comparable {

	private String value;

	public int compareTo(Object another) {
		return super.compareTo(another);
	}

	public CauseType getCauseType() {
		return Cause.CauseType.RANGE_CHECK_CAUSE;
	}

	public MissingNumberPattern(Field field, String evaluator, String value) {
		this.field = field;
		this.evaluator = evaluator;
		this.value = value;
	}

	/**
	 * Returns alway null, because there is no rule that this is related to.
	 */
	public String getRuleName() {
		return null;
	}

	public String getValueAsString() {
		return value;
	}

	@Override
	public String toString() {
		return "Missing restriction " + evaluator + " " + value;
	}
}
