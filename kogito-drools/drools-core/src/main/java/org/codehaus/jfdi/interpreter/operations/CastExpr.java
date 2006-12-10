package org.codehaus.jfdi.interpreter.operations;

public class CastExpr implements Expr {
	
	private Class type;
	private Expr expr;

	public CastExpr(Class type, Expr expr) {
		this.type = type;
		this.expr = expr;
	}

	public Object getValue() {
		return expr.getValue();
	}

	public Class getType() {
		return type;
	}

}
