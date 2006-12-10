package org.drools.base;

import java.util.Map;

import org.codehaus.jfdi.interpreter.VariableValueHandler;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.FieldExtractor;

public class DroolsJFDIGlobalVariable
    implements
    VariableValueHandler {
    
    private String identifier;
    private Class type;
    private DroolsJFDIFactory factory;
       
    public DroolsJFDIGlobalVariable(String identifier,
                                    Class type,
                                    DroolsJFDIFactory factory ) {
        this.identifier = identifier;
        this.factory =  factory;
        this.type = type;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }

    public Class getType() {
        return this.type;
    }

    public Object getValue() {
        return this.factory.getValue( this.identifier );
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
        throw new UnsupportedOperationException( "External Variable identifer='" + getIdentifier() + "' type='" + getType() + "' is final, it cannot be set" );
    }

}
