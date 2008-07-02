package org.drools.base.mvel;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import org.mvel.integration.VariableResolver;

public class DroolsMVELGlobalVariable
    implements
    VariableResolver,
    Externalizable {

    private static final long serialVersionUID = -2480015657934353449L;
    
    private String            name;
    private Class             knownType;
    private DroolsGlobalVariableMVELFactory factory;

    public DroolsMVELGlobalVariable() {
    }

    public DroolsMVELGlobalVariable(final String identifier,
                                    final Class knownType,
                                    final DroolsGlobalVariableMVELFactory factory) {
        this.name = identifier;
        this.factory = factory;
        this.knownType = knownType;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
        knownType   = (Class)in.readObject();
        factory     = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(knownType);
        out.writeObject(factory);
    }

    public String getName() {
        return this.name;
    }

    public Class getKnownType() {
        return this.knownType;
    }

    public Object getValue() {
        return this.factory.getValue( this.name );
    }

    public void setValue(final Object value) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getName() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }

    public int getFlags() {
        return 0;
    }

    /**
     * Not used in drools.
     */
    public Class getType() {
        return this.knownType;
    }

    /**
     * Not used in drools.
     */
    public void setStaticType(Class arg0) {
    }

}
