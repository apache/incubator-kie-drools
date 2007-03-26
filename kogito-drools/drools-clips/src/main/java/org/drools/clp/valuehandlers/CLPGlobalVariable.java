package org.drools.clp.valuehandlers;

import org.drools.clp.CLPFactory;
import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;
import org.drools.clp.VariableValueHandler;


public class CLPGlobalVariable extends BaseValueHandler implements VariableValueHandler  {
    
    private String identifier;
    private Class knownType;
    private int simpleType;
    private CLPFactory factory;
       
    public CLPGlobalVariable(String identifier,
                                    Class knownType,
                                    int simpleType,
                                    CLPFactory factory ) {
        this.identifier = identifier;
        this.factory =  factory;
        this.knownType = knownType;
    }    
    
    public ValueHandler getValue(ExecutionContext context) {
        return null;
    }    
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    public int getValueType(ExecutionContext context) {
        return this.simpleType;
    }    

    public Class getKnownType() {
        return this.knownType;
    }

    public Object getObject(ExecutionContext context) {
        return this.factory.getValue( this.identifier );
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getIdentifier() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }       
    
    public String toString() {
        String name = getClass().getName();
        name = name.substring( name.lastIndexOf( "." ) + 1 );
        return "[" + name + " identifier = '" + getIdentifier()  + "']";
    }        

}
