package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.ValueType;

public class LocalVariableValue implements VariableValueHandler {
    private static final long serialVersionUID = 320L;    

    /** The identifier for the variable. */
    private final String      identifier;    
    private final int         index;
    
    /**
     * Construct.
     * 
     * @param identifier
     *            The name of the variable.
     */
    public LocalVariableValue(final String identifier, final int index) {
        this.identifier = identifier;
        this.index = index;
    }
    
    /**
     * Retrieve the variable's identifier.
     * 
     * @return The variable's identifier.
     */
    public String getIdentifier() {
        return this.identifier;
    }
    
    public ValueType getValueType() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Object getValue(ExecutionContext context) {
        return context.getLocalVariable( this.index );
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        context.setLocalVariable( this.index, object );        
    }        

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

