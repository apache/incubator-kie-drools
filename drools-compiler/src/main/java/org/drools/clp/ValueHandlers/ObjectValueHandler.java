package org.drools.clp.ValueHandlers;

import org.drools.base.SimpleValueType;
import org.drools.clp.ExecutionContext;

public class ObjectValueHandler extends BaseValueHandler {
    public static final ObjectValueHandler NULL = new ObjectValueHandler( "null" );    
    
    private Object objectValue;
    
    public int getValueType(ExecutionContext context) {
        return SimpleValueType.OBJECT;
    }
    
    public ObjectValueHandler(Object objectValue) {
        this.objectValue = objectValue;
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new RuntimeException( "LiteralValues cannot be set");
    }
    
    public Object getValue(ExecutionContext context) {
        return this.objectValue;
    }
    /*
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
    }      */

}
