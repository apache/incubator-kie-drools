package org.drools.clp.valuehandlers;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.SimpleValueType;
import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;

public class DoubleValueHandler implements ValueHandler {
    private double doubleValue;
    
    public DoubleValueHandler(String doubleValue) {
        this.doubleValue = Double.valueOf( doubleValue ).doubleValue();
    }
    
    public DoubleValueHandler(double doubleValue) {
        this.doubleValue = doubleValue;
    }   
    
    public ValueHandler getValue(ExecutionContext context) {
        return null;
    }    
    
    public int getValueType(ExecutionContext context) {
        return SimpleValueType.DECIMAL;
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new RuntimeException( "LiteralValues cannot be set");
    }
    
    public Object getObject(ExecutionContext context) {
        return new Double( this.doubleValue );
    }
    
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        return new BigDecimal( this.doubleValue );
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        return BigInteger.valueOf( (long) this.doubleValue );
    }

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        return ( this.doubleValue == 0 ) ? false : true;
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        return this.doubleValue;
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        return (float) this.doubleValue;
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        return (int) this.doubleValue;
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        return (long) this.doubleValue;
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        return (short) this.doubleValue;
    }

    public String getStringValue(ExecutionContext context) {
        return Double.toString( this.doubleValue );
    }

    public String toString() {
        return "[DoubleLiteralValue value='" + this.doubleValue + "']";
    }    
    
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits( doubleValue );
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof DoubleValueHandler) ) return false;
        final DoubleValueHandler other = (DoubleValueHandler) obj;
        if ( Double.doubleToLongBits( doubleValue ) != Double.doubleToLongBits( other.doubleValue ) ) return false;
        return true;
    }
    
    
}
