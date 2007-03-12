package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SlotNameValuePair implements ValueHandler {
    private String name;
    private ValueHandler value;
    
    public SlotNameValuePair(String name, ValueHandler value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ValueHandler getValueHandler() {
        return this.value;
    }

    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "cannot getBigDecimalValue from SlotNameValuePair" );
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "cannot getBigIntegerValue from SlotNameValuePair" );
    }

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        throw new RuntimeException( "cannot getBooleanValue from SlotNameValuePair" );
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "cannot getDoubleValue from SlotNameValuePair" );
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "cannot getFloatValue from SlotNameValuePair" );
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "cannot getIntValue from SlotNameValuePair" );
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "cannot getLongValue from SlotNameValuePair" );
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "cannot getShortValue from SlotNameValuePair" );
    }

    public String getStringValue(ExecutionContext context) {
        throw new RuntimeException( "cannot getStringValue from SlotNameValuePair" );
    }

    public Object getValue(ExecutionContext context) {
        throw new RuntimeException( "cannot getValue from SlotNameValuePair" );
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        throw new RuntimeException( "cannot setValue on SlotNameValuePair" );
        
    }
    
    
    
    
}
