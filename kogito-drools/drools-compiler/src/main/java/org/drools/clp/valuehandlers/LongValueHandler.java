package org.drools.clp.valuehandlers;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.SimpleValueType;
import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;

public class LongValueHandler implements ValueHandler {
    private long longValue;
    
    public LongValueHandler(String longValue) {
        this.longValue = Long.valueOf( longValue ).longValue();
    } 
    
    public int getValueType(ExecutionContext context) {
        return SimpleValueType.INTEGER;
    }    
    
    public LongValueHandler(long longValue) {
        this.longValue = longValue;
    }   
    
    public ValueHandler getValue(ExecutionContext context) {
        return null;
    }      

    public void setValue(ExecutionContext context, Object value) {
        throw new RuntimeException( "LiteralValues cannot be set");
    }
    
    public Object getObject(ExecutionContext context) {
        return new Long( this.longValue );
    }
    
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        return new BigDecimal( this.longValue );
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        return BigInteger.valueOf( this.longValue );
    }

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        return ( this.longValue == 0 ) ? false : true;
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        return this.longValue;
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        return this.longValue;
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        return (int) this.longValue;
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        return this.longValue;
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        return (short) this.longValue;
    }

    public String getStringValue(ExecutionContext context) {
        return Long.toString( this.longValue );
    }
    
    public String toString() {
        return "[LongLiteralValue value='" + this.longValue + "']";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (longValue ^ (longValue >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( ! ( obj instanceof LongValueHandler ) ) return false;
        final LongValueHandler other = (LongValueHandler) obj;
        if ( longValue != other.longValue ) return false;
        return true;
    }      
    
    
}
