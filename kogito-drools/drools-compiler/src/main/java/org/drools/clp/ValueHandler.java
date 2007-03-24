package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ValueHandler {
    public boolean isResolved();
    
    public int getValueType(ExecutionContext context);
    
    public void setValue(ExecutionContext context,
                         Object object);

    public Object getValue(ExecutionContext context);

    public String getStringValue(ExecutionContext context);

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException;

    public int getIntValue(ExecutionContext context) throws NumberFormatException;

    public short getShortValue(ExecutionContext context) throws NumberFormatException;

    public long getLongValue(ExecutionContext context) throws NumberFormatException;

    public float getFloatValue(ExecutionContext context) throws NumberFormatException;

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException;

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException;

    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException;
        
}