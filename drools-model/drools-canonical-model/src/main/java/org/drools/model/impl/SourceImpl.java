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
}
