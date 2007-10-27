package org.drools.analytics.components;

import java.io.Serializable;

/**
 * 
 * @author Toni Rikkola
 */
public class OperatorDescr extends AnalyticsComponent implements Serializable {
	private static final long serialVersionUID = 8393994152436331910L;

	private static int index = 0;

	public static enum Type {
		AND, OR
	};

	private Type type;

	public OperatorDescr() {
		super(index++);
	}

	public OperatorDescr(Type operatorType) {
		super(index++);
		this.type = operatorType;
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.OPERATOR;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
