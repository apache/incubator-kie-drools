package org.codehaus.jfdi.interpreter.operations;

import org.codehaus.jfdi.SymbolTable;

public class MemberAccessExpr implements Expr {
	
	private Expr lhs;
	private String name;
	private Expr[] args;

	public MemberAccessExpr(Expr lhs, String name, Expr[] args) {
		this.lhs = lhs;
		this.name = name;
		this.args = args;
	}

	public Object evaluate(SymbolTable symbolTable) {
		return null;
	}

	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Class getType() {
		return Object.class;
	}

}
