package org.codehaus.jfdi.interpreter;

import java.util.Map;

public class DefaultExternalVariable
    implements
    VariableValueHandler {
    private Map context;
    
    private Object cachedValue;
    
    private String identifer;
    private Class type;
    
    public DefaultExternalVariable(String identifier,
                                   Map context) {
        this.identifer = identifier;
        this.context = context;
    }
    
    public String getIdentifier() {
        return this.identifer;
    }

    public Class getType() {
        return getValue().getClass();
    }

    public Object getValue() {
        if ( this.cachedValue  ==  null ) {
            this.cachedValue = this.context.get( this.identifer );
        }
        return this.cachedValue;
    }

    public boolean isFinal() {
        return true;
    }

    public boolean isLiteral() {
        return false;
    }

    public boolean isLocal() {
        return false;
    }

    public void setValue(Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + this.identifer + "' type='" + this.type + "' is final, it cannot be set" );
    }

}
