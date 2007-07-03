package org.drools.clp.valuehandlers;

import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;
import org.drools.clp.VariableValueHandler;


public class NamedShellVariableValue extends BaseValueHandler implements VariableValueHandler {
    private static final long serialVersionUID = 400L;    

    /** The identifier for the variable. */
    private final String      identifier;    
    
    /**
     * Construct.
     * 
     * @param identifier
     *            The name of the variable.
     */
    public NamedShellVariableValue(final String identifier) {
        this.identifier = identifier;
    }
        
    /**
     * Retrieve the variable's identifier.
     * 
     * @return The variable's identifier.
     */
    public String getIdentifier() {
        return this.identifier;
    }
    
    public int getValueType(ExecutionContext context) {
        return context.getShellVariable( this.identifier ).getValueType( context );
    }
    
    public Object getObject(ExecutionContext context) {        
        return getValue(context).getObject( context );
    }        
    
    public ValueHandler getValue(ExecutionContext context) {
        ValueHandler valueHandler = context.getShellVariable( this.identifier );
        ValueHandler nested; 
        while ( (nested = valueHandler.getValue( context ) ) != null ) {
            valueHandler = nested;
        }
        return valueHandler;
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        context.setShellVariable( this.identifier, (ValueHandler) object );        
    }        
    
    public String toString() {
        String name = getClass().getName();
        name = name.substring( name.lastIndexOf( "." ) + 1 );
        return "[" + name + " identifier = '" + getIdentifier()  + "']";
    }    
        
}

