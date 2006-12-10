package org.codehaus.jfdi.interpreter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Coercion {
	
	public static boolean toBoolean(Object obj) {
		if ( obj instanceof Boolean ) {
			return ((Boolean)obj).booleanValue();
		}
		
		throw new CoercionException( obj, "boolean" );
	}
	
	public static int toInteger(Object obj) {
		if ( obj instanceof Number ) {
			return ((Number)obj).intValue();
		}
		throw new CoercionException( obj, "integer" );
	}
	
	public static BigInteger toBigInteger(Object object) {
		if ( object instanceof BigInteger ) {
			return (BigInteger) object;
		}
		
		if ( object instanceof BigDecimal ) {
			return ((BigDecimal)object).toBigInteger();
		}
		
		return new BigInteger( object.toString() );
	}
	
	public static double toFloat(Object obj) {
		if ( obj instanceof Number ) {
			return ((Number)obj).doubleValue();
		}
		throw new CoercionException( obj, "floating-point" );
	}
	
	public static BigDecimal toBigDecimal(Object object) {
		if ( object instanceof BigDecimal ) {
			return (BigDecimal) object;
		}
		
		return new BigDecimal( object.toString() );
	}
}
	
