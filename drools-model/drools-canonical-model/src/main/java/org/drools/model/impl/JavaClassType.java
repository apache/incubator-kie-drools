package org.drools.model.impl;

import org.drools.model.Type;

public class JavaClassType<T> implements Type<T> {

    private final Class<T> type;

    public JavaClassType(Class<T> type) {
        this.type = type;
    }

    public boolean isInstance(Object obj) {
        return type.isInstance(obj);
    }

    @Override
    public String toString() {
        return type.getName();
    }

    @Override
    public Class<T> asClass() {
        return type;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof JavaClassType) ) return false;

        JavaClassType<?> that = ( JavaClassType<?> ) o;

        return type.getName().equals( that.type.getName() );
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
