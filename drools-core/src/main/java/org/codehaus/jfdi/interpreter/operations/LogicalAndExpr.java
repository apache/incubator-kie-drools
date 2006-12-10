package org.codehaus.jfdi.interpreter.operations;

import org.codehaus.jfdi.SymbolTable;
import org.codehaus.jfdi.interpreter.Coercion;

public class LogicalAndExpr implements Expr {
	
	private Expr lhs;
	private Expr rhs;

	public LogicalAndExpr(Expr lhs, Expr rhs) {
		System.err.println( "LogicalAndExpr l: " + lhs );
		System.err.println( "LogicalAndExpr r: " + rhs );
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public Object getValue() {
		System.err.println( "L: " + lhs );
		System.err.println( "R: " + rhs );
		boolean lhsBool = Coercion.toBoolean( lhs.getValue() );
		if ( ! lhsBool ) {
			// short-circuit, avoid rhs
			return Boolean.FALSE;
		}
		
		boolean rhsBool = Coercion.toBoolean( rhs.getValue() );
		
		return ( rhsBool ? Boolean.TRUE : Boolean.FALSE );
	}
	
	public Class getType() {
		return Boolean.class;
	}

}
