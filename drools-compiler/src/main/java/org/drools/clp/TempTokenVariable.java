package org.drools.clp;

import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.mvel.integration.VariableResolver;

public class TempTokenVariable extends BaseValueHandler implements VariableValueHandler {

    private String identifier;
    
    public TempTokenVariable(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Object getValue(ExecutionContext context) {
        throw new UnsupportedOperationException( "TempTokenVariable Variable identifer='" + getIdentifier() + " cannot be read" );
    }

    public void setValue(ExecutionContext context, Object value) {
        throw new UnsupportedOperationException( "TempTokenVariable Variable identifer='" + getIdentifier() + " cannot be set" );
    }

    public void setContext(ExecutionContext context) {

    }

}
