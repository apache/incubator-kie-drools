package org.drools.model.impl;

import org.drools.model.EntryPoint;

public class EntryPointImpl implements EntryPoint, ModelComponent {

    private final String name;

    public EntryPointImpl( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof EntryPointImpl) ) return false;

        EntryPointImpl that = ( EntryPointImpl ) o;

        return name != null ? name.equals( that.name ) : that.name == null;
    }
}
