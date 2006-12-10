package org.codehaus.jfdi.interpreter.operations;

import org.codehaus.jfdi.interpreter.Coercion;
import org.codehaus.jfdi.interpreter.CoercionException;


public class LogicalOrExpr implements Expr {
	
	private Expr lhs;
	private Expr rhs;

	public LogicalOrExpr(Expr lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public Object getValue() {
		boolean lhsBool = Coercion.toBoolean( lhs.getValue() );
		if ( lhsBool ) {
			// short-circuit, avoid rhs
			return Boolean.TRUE;
		}
		
		boolean rhsBool = Coercion.toBoolean( rhs.getValue() );
		
		return ( rhsBool ? Boolean.TRUE : Boolean.FALSE );
	}
	
	public Class getType() {
		return Boolean.class;
	}

}
