package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BooleanLiteralValue extends BaseValueHandler {
    private boolean booleanValue;
    
    public BooleanLiteralValue(String booleanValue) {
        this.booleanValue = Boolean.valueOf( booleanValue ).booleanValue();
    } 
    
    public BooleanLiteralValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new RuntimeException( "LiteralValues cannot be set");
    }
    
    public Object getValue(ExecutionContext context) {
        return new Boolean( this.booleanValue );
    }
    
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "Boolean format exception, cannot be cast to BitDecimal" );
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "Boolean format exception, cannot be cast to BitInteger" );
    }

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        return this.booleanValue;
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "Boolean format exception, cannot be cast to double" );
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "Boolean format exception, cannot be cast to float" );
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "Boolean format exception, cannot be cast to int" );
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "Boolean format exception, cannot be cast to long" );
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "Boolean format exception, cannot be cast to short" ); 
    }

    public String getStringValue(ExecutionContext context) {
        return Boolean.toString( this.booleanValue );
    }      
}
