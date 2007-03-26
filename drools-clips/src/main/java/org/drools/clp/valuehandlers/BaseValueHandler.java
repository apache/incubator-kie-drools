package org.drools.clp.valuehandlers;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;

public abstract class BaseValueHandler implements ValueHandler {        
    
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        Object object = getObject( context );        
        if ( object instanceof BigDecimal ) {
            return (BigDecimal) object;
        } else {
            return new BigDecimal( object.toString() );
        }
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        Object object = getObject( context );
        if ( object instanceof BigInteger ) {
            return (BigInteger) object;
        } else {
            return new BigInteger( object.toString() );
        }
    }

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        return ((Boolean) getObject(context)).booleanValue();
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        return ((Double) getObject(context)).doubleValue();
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        return ((Float) getObject(context)).floatValue();
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        return ((Integer) getObject(context)).intValue();
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        return ((Long) getObject(context)).longValue();
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        return ((Short) getObject(context)).shortValue();
    }

    public String getStringValue(ExecutionContext context) {
        return (String) getObject(context);
    }      
    
    public String toString() {
        String name = getClass().getName();
        name = name.substring( name.lastIndexOf( "." ) + 1 );
        try {
            return "[" + name + " value = '" + getObject(null) + "']";
        } catch (Exception e) {
            return "[" + name + " value = N/A]";
        }
    }
    
}
