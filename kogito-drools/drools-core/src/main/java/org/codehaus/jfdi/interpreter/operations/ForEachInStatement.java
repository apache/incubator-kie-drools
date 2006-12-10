package org.codehaus.jfdi.interpreter.operations;

import org.codehaus.jfdi.SymbolTable;

public class ForEachInStatement implements Statement {
	
	private String iteratorName;
	private Expr collection;

	public ForEachInStatement(String iteratorName, Expr collection) {
		this.iteratorName = iteratorName;
		this.collection = collection;
	}

	public void execute(SymbolTable symbolTable) {
	}
	

}
