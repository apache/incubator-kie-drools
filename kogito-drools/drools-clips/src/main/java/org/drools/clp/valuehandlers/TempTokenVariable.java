package org.drools.clp.valuehandlers;

import org.drools.base.SimpleValueType;
import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;
import org.drools.clp.VariableValueHandler;


public class TempTokenVariable extends BaseValueHandler implements VariableValueHandler {

    private String identifier;
    
    public TempTokenVariable(String identifier) {
        this.identifier = identifier;
    }
    
    public ValueHandler getValue(ExecutionContext context) {
        return this;
    }    

    public String getIdentifier() {
        return this.identifier;
    }
    
    public int getValueType(ExecutionContext context) {
        return SimpleValueType.UNKNOWN;
    }    

    public Object getObject(ExecutionContext context) {
        throw new UnsupportedOperationException( "TempTokenVariable Variable identifer='" + getIdentifier() + " cannot be read" );
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new UnsupportedOperationException( "TempTokenVariable Variable identifer='" + getIdentifier() + " cannot be set" );
    }

    public void setContext(ExecutionContext context) {

    }

}
