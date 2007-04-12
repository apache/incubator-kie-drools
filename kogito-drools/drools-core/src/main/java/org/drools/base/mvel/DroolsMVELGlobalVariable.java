package org.drools.base.mvel;

import org.mvel.integration.VariableResolver;

public class DroolsMVELGlobalVariable
    implements
    VariableResolver {

    private String            name;
    private Class             knownType;
    private DroolsMVELFactory factory;

    public DroolsMVELGlobalVariable(final String identifier,
                                    final Class knownType,
                                    final DroolsMVELFactory factory) {
        this.name = identifier;
        this.factory = factory;
        this.knownType = knownType;
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

}
