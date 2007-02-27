package org.drools.base.mvel;

import org.drools.rule.Declaration;
import org.mvel.integration.VariableResolver;

public class DroolsMVELLocalDeclarationVariable
    implements
    VariableResolver {
    
    private Declaration declaration;
    private DroolsMVELFactory factory;
       
    public DroolsMVELLocalDeclarationVariable(Declaration declaration,
    		DroolsMVELFactory factory ) {
        this.declaration = declaration;
        this.factory =  factory;
    }        
    
    public String getName() {
        return this.declaration.getIdentifier();
    }

    public Class getKnownType() {
        return declaration.getExtractor().getExtractToClass();
    }

    public Object getValue() {
        return declaration.getValue( this.factory.getObject() );
    }

    public void setValue(Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getName() + "' type='" + getKnownType()+ "' is final, it cannot be set" );
    }
    
    public int getFlags()  {
    	return 0;
    }    

}
