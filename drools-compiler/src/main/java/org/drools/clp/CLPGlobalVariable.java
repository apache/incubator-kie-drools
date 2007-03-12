package org.drools.clp;

import org.mvel.integration.VariableResolver;

public class CLPGlobalVariable extends BaseValueHandler {
    
    private String name;
    private Class knownType;
    private CLPFactory factory;
       
    public CLPGlobalVariable(String identifier,
                                    Class knownType,
                                    CLPFactory factory ) {
        this.name = identifier;
        this.factory =  factory;
        this.knownType = knownType;
    }
    
    public String getName() {
        return this.name;
    }

    public Class getKnownType() {
        return this.knownType;
    }

    public Object getValue(ExecutionContext context) {
        return this.factory.getValue( this.name );
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getName() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }        

}
