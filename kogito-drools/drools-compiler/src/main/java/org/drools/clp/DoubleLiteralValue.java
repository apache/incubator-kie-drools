package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DoubleLiteralValue extends BaseValueHandler {
    private double doubleValue;
    
    public DoubleLiteralValue(String doubleValue) {
        this.doubleValue = Double.valueOf( doubleValue ).doubleValue();
    }
    
    public DoubleLiteralValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new RuntimeException( "LiteralValues cannot be set");
    }
    
    public Object getValue(ExecutionContext context) {
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
}
