package org.codehaus.jfdi.interpreter.operations;

import org.codehaus.jfdi.SymbolTable;

public class AssignmentStatement implements Statement {
	
	private Expr lhs;
	private Expr rhs;

	public AssignmentStatement(Expr lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public void execute(SymbolTable symbolTable) {
		
	}

}
