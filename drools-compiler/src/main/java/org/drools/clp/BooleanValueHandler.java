package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.SimpleValueType;

public class BooleanValueHandler implements ValueHandler {
    private boolean booleanValue;
    
    public BooleanValueHandler(String booleanValue) {
        this.booleanValue = Boolean.valueOf( booleanValue ).booleanValue();
    } 
    
    public BooleanValueHandler(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }
    
    public int getValueType(ExecutionContext context) {
        return SimpleValueType.BOOLEAN;
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
    
    public String toString() {
        return "[BooleanLiteralValue value='" + this.booleanValue + "']";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (booleanValue ? 1231 : 1237);
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof BooleanValueHandler) ) return false;
        final BooleanValueHandler other = (BooleanValueHandler) obj;
        if ( booleanValue != other.booleanValue ) return false;
        return true;
    }
    
    
}
