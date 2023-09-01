package org.drools.model.impl;

import org.drools.model.Prototype;
import org.drools.model.PrototypeFact;
import org.drools.model.PrototypeVariable;

public class PrototypeVariableImpl extends DeclarationImpl<PrototypeFact> implements PrototypeVariable {

    private final Prototype prototype;

    public PrototypeVariableImpl( Prototype prototype ) {
        super( PrototypeFact.class );
        this.prototype = prototype;
    }

    public PrototypeVariableImpl( Prototype prototype, String name ) {
        super( PrototypeFact.class, name );
        this.prototype = prototype;
    }

    @Override
    public Prototype getPrototype() {
        return prototype;
    }
}
