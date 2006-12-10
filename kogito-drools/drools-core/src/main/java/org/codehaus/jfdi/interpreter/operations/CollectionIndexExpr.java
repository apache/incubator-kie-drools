package org.codehaus.jfdi.interpreter.operations;

import org.codehaus.jfdi.SymbolTable;

public class CollectionIndexExpr implements Expr {
	
	private Expr lhs;
	private Expr index;

	public CollectionIndexExpr(Expr lhs, Expr index) {
		this.lhs = lhs;
		this.index = index;
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
