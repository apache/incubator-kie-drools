package org.drools.model.impl;

import org.drools.model.Type;
import org.drools.model.Variable;

import static org.drools.model.impl.NamesGenerator.generateName;

public abstract class VariableImpl<T> implements Variable<T>, ModelComponent {

    private final Type<T> type;
    private final String name;

    public VariableImpl(Type<T> type) {
        this(type, generateName("var"));
    }

    public VariableImpl(Type<T> type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Type<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Variable " + name + " of type " + type;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Variable var = ( Variable ) o;
        return type.equals( var.getType() );
    }
}
