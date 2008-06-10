package org.drools.verifier.components;

import java.io.Serializable;

/**
 * 
 * @author Toni Rikkola
 */
public class OperatorDescr extends VerifierComponent implements Serializable {
	private static final long serialVersionUID = 8393994152436331910L;

	private static int index = 0;

	public static class Type {
		public static final Type AND = new Type(0);
		public static final Type OR = new Type(1);

		private final int index;

		private Type(int i) {
			index = i;
		}
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
	public VerifierComponentType getComponentType() {
		return VerifierComponentType.OPERATOR;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
