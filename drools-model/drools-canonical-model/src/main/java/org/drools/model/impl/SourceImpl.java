package org.drools.model.impl;

import org.drools.model.Source;
import org.drools.model.Type;

public class SourceImpl<T> implements Source<T> {
    private final String name;
    private final Type<T> type;

    public SourceImpl( String name, Type<T> type ) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Source of type " + type;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof SourceImpl) ) return false;

        SourceImpl<?> source = ( SourceImpl<?> ) o;

        if ( name != null ? !name.equals( source.name ) : source.name != null ) return false;
        return type != null ? type.equals( source.type ) : source.type == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
