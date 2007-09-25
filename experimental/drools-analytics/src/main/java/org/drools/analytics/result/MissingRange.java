package org.drools.analytics.result;

import org.drools.analytics.components.Field;

public abstract class MissingRange implements Comparable {

	private static int index = 0;
	protected int id = index++;

	protected Field field;
	protected String evaluator;

	private String firedRuleName;

	public int compareTo(Object another) {
		if (!(another instanceof MissingRange)) {
			throw new ClassCastException("A MissingRange object expected.");
		}

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

	public String getEvaluator() {
		return evaluator;
	}

	public String getFiredRuleName() {
		return firedRuleName;
	}

	public void setFiredRuleName(String firedRuleName) {
		this.firedRuleName = firedRuleName;
	}
}
