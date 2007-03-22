package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.ValueType;

public class LocalVariableValue extends BaseValueHandler implements VariableValueHandler {
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
        return null;
    }
    
    public Object getValue(ExecutionContext context) {
        return context.getLocalVariable( this.index );
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        context.setLocalVariable( this.index, object );        
    }        
        
}

