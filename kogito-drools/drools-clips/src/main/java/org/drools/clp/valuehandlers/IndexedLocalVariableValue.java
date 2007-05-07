package org.drools.clp.valuehandlers;

import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;
import org.drools.clp.VariableValueHandler;


public class IndexedLocalVariableValue extends BaseValueHandler implements VariableValueHandler {
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
    public IndexedLocalVariableValue(final String identifier, final int index) {
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
    
    public int getValueType(ExecutionContext context) {
        return context.getLocalVariable( this.index ).getValueType( context );
    }
    
    public Object getObject(ExecutionContext context) {        
        return getValue(context).getObject( context );
    }        
    
    public ValueHandler getValue(ExecutionContext context) {
        ValueHandler valueHandler = context.getLocalVariable( this.index );
        ValueHandler nested; 
        while ( (nested = valueHandler.getValue( context ) ) != null ) {
            valueHandler = nested;
        }
        return valueHandler;
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        context.setLocalVariable( this.index, (ValueHandler) object );        
    }        
    
    public String toString() {
        String name = getClass().getName();
        name = name.substring( name.lastIndexOf( "." ) + 1 );
        return "[" + name + " identifier = '" + getIdentifier()  + "']";
    }    
        
}

