package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.mvel.integration.VariableResolver;

public class DroolsMVELLocalDeclarationVariable
    implements
    VariableResolver,
    Externalizable  {

    private static final long serialVersionUID = -8081447010148362496L;
    
    private Declaration       declaration;
    private DroolsMVELFactory factory;

    public DroolsMVELLocalDeclarationVariable() {
    }

    public DroolsMVELLocalDeclarationVariable(final Declaration declaration,
                                              final DroolsMVELFactory factory) {
        this.declaration = declaration;
        this.factory = factory;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        declaration = (Declaration)in.readObject();
        factory     = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(declaration);
        out.writeObject(factory);
    }

    public String getName() {
        return this.declaration.getIdentifier();
    }

    public Class getKnownType() {
        return this.declaration.getExtractor().getExtractToClass();
    }

    public Object getValue() {
        return this.declaration.getValue( (InternalWorkingMemory) this.factory.getWorkingMemory(), this.factory.getObject() );
    }

    public void setValue(final Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getName() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }

    public int getFlags() {
        return 0;
    }

    public Class getType() {
        return this.declaration.getExtractor().getExtractToClass();
    }
    
    /**
     * Not used in drools.
     */
    public void setStaticType(Class arg0) {
    }

}
