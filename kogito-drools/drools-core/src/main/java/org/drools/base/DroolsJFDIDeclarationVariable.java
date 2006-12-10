package org.drools.base;

import java.util.Map;

import org.codehaus.jfdi.interpreter.VariableValueHandler;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.FieldExtractor;

public class DroolsJFDIDeclarationVariable
    implements
    VariableValueHandler {
    
    private Declaration declaration;
    private DroolsJFDIFactory factory;
       
    public DroolsJFDIDeclarationVariable(Declaration declaration,
                                      DroolsJFDIFactory factory ) {
        this.declaration = declaration;
        this.factory =  factory;
    }
    
    public String getIdentifier() {
        return this.declaration.getIdentifier();
    }

    public Class getType() {
        return declaration.getExtractor().getExtractToClass();
    }

    public Object getValue() {
        return declaration.getValue( this.factory.getValue( this.declaration ));
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
