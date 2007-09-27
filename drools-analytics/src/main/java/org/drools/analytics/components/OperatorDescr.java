package org.drools.analytics.components;

import java.io.Serializable;

/**
 * 
 * @author Toni Rikkola
 */
public class OperatorDescr extends AnalyticsComponent implements Serializable {
	private static final long serialVersionUID = 8393994152436331910L;

	private static int index = 0;

	private static final OperatorDescr OR_OPERATOR = new OperatorDescr(Type.OR);
	private static final OperatorDescr AND_OPERATOR = new OperatorDescr(Type.AND);

	public static enum Type {
		AND, OR
	};

	private Type type;

	private OperatorDescr() {
		super(index++);
	}

	public static OperatorDescr valueOf(Type type) {
		switch (type) {
		case OR:
			return OR_OPERATOR;
		case AND:
			return AND_OPERATOR;
		default:
			return null;
		}
	}

	@Override
	public AnalyticsComponentType getComponentType() {
		return AnalyticsComponentType.OPERATOR;
	}

	private OperatorDescr(Type operatorType) {
		super(index++);
		this.type = operatorType;
	}

	public Type getType() {
		return type;
	}
}
