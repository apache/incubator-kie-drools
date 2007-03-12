package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.ValueType;

public abstract class BaseValueHandler implements ValueHandler {        
    
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        return (BigDecimal) getValue(context);
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        return (BigInteger) getValue(context);
    }

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        return ((Boolean) getValue(context)).booleanValue();
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        return ((Double) getValue(context)).doubleValue();
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        return ((Float) getValue(context)).floatValue();
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        return ((Integer) getValue(context)).intValue();
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        return ((Long) getValue(context)).longValue();
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        return ((Short) getValue(context)).shortValue();
    }

    public String getStringValue(ExecutionContext context) {
        return (String) getValue(context);
    }      
    

}
