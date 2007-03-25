package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ValueHandler {    
    public int getValueType(ExecutionContext context);

    public void setValue(ExecutionContext context,
                         Object object);

    /**
     * 
     */
    public ValueHandler getValue(ExecutionContext context);
    
    /**
     * Resolves to a java Object
     */
    public Object getObject(ExecutionContext context);

    /**
     * Resolves to a String, if possible
     */
    public String getStringValue(ExecutionContext context);

    /**
     * Resolves to a boolean, if possible
     */
    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException;

    /**
     * Resolves to a int, if possible
     */    
    public int getIntValue(ExecutionContext context) throws NumberFormatException;

    /**
     * Resolves to a short, if possible
     */    
    public short getShortValue(ExecutionContext context) throws NumberFormatException;

    /**
     * Resolves to a long, if possible
     */    
    public long getLongValue(ExecutionContext context) throws NumberFormatException;

    /**
     * Resolves to a float, if possible
     */    
    public float getFloatValue(ExecutionContext context) throws NumberFormatException;

    /**
     * Resolves to a double, if possible
     */    
    public double getDoubleValue(ExecutionContext context) throws NumberFormatException;

    /**
     * Resolves to a BigInteger, if possible
     */    
    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException;

    /**
     * Resolves to a BigDecimal, if possible
     */     
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException;

}