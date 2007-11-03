package org.drools.analytics.report.components;

import org.drools.analytics.components.Field;
import org.drools.base.evaluators.Operator;

public abstract class MissingRange implements Comparable<MissingRange> {

	private static int index = 0;
	protected int id = index++;

	protected Field field;
	protected Operator operator;

	private String firedRuleName;

	/**
	 * Takes the given operator e, and returns a reversed version of it.
	 * 
	 * @return operator
	 */
	public static Operator getReversedOperator(Operator e) {
		if (e.equals(Operator.NOT_EQUAL)) {
			return Operator.EQUAL;
		} else if (e.equals(Operator.EQUAL)) {
			return Operator.NOT_EQUAL;
		} else if (e.equals(Operator.GREATER)) {
			return Operator.LESS_OR_EQUAL;
		} else if (e.equals(Operator.LESS)) {
			return Operator.GREATER_OR_EQUAL;
		} else if (e.equals(Operator.GREATER_OR_EQUAL)) {
			return Operator.LESS;
		} else if (e.equals(Operator.LESS_OR_EQUAL)) {
			return Operator.GREATER;
		} else if (e.equals(Operator.MATCHES)) {
			return Operator.NOT_MATCHES;
		} else if (e.equals(Operator.NOT_MATCHES)) {
			return Operator.MATCHES;
		} else if (e.equals(Operator.CONTAINS)) {
			return Operator.NOT_CONTAINS;
		} else if (e.equals(Operator.NOT_CONTAINS)) {
			return Operator.CONTAINS;
		} else if (e.equals(Operator.MEMBEROF)) {
			return Operator.NOTMEMBEROF;
		} else if (e.equals(Operator.NOTMEMBEROF)) {
			return Operator.MEMBEROF;
		}

		return e;
	}

	public int compareTo(MissingRange another) {
		MissingRange anotherMissingRange = ((MissingRange) another);

		return this.id - anotherMissingRange.getId();
	}

	public int getId() {
		return id;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Operator getOperator() {
		return operator;
	}

	public String getFiredRuleName() {
		return firedRuleName;
	}

	public void setFiredRuleName(String firedRuleName) {
		this.firedRuleName = firedRuleName;
	}
}
