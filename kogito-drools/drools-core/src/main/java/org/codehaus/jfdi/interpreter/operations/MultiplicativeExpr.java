package org.codehaus.jfdi.interpreter.operations;

import org.codehaus.jfdi.SymbolTable;
import org.codehaus.jfdi.interpreter.Coercion;

public class MultiplicativeExpr implements Expr {
	
	public static class Operator { 
		private String str;
		public Operator(String str) {
			this.str = str;
		}
		
		public String toString() {
			return str;
		}
		
	}
	public static final Operator MULT = new Operator( "*" );
	public static final Operator DIV = new Operator( "/" );
	
	private Expr lhs;
	private Expr rhs;
	private Object op;

	public MultiplicativeExpr(Expr lhs, Expr rhs, Operator op) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}

	public Object getValue() {
		
		Object lhsObj = lhs.getValue();
		Object rhsObj = rhs.getValue();
		
		if ( lhsObj instanceof Integer || rhsObj instanceof Integer ) {
			int lhsInt = Coercion.toInteger( lhs.getValue() );
			int rhsInt = Coercion.toInteger( rhs.getValue() );
			
			int result = 0;
		
			if ( op == MULT ) {
				result = lhsInt * rhsInt;
			} else {
				result = lhsInt / rhsInt;
			}
			
			return new Integer( result );
		}
		
		if ( lhsObj instanceof Double || rhsObj instanceof Double ) {
			double lhsDouble = Coercion.toFloat( lhs.getValue() );
			double rhsDouble = Coercion.toFloat( rhs.getValue() );
			
			double result = 0;
			
			if ( op == MULT ) {
				result = lhsDouble * rhsDouble;
			} else {
				result = lhsDouble / rhsDouble;
			}
			
			return new Double( result );
		}
		
		throw new ArithmeticException( lhsObj.toString() + op.toString() + rhsObj.toString() );
	}
	
	public Class getType() {
		
		if ( lhs.getType() == Integer.class || rhs.getType() == Integer.class ) {
			return Integer.class;
		}
		
		if ( lhs.getType() == Double.class || rhs.getType() == Double.class ) {
			return Double.class;
		}
		
		throw new ArithmeticException( "invalid arithmetic types" );
	}
	

}
