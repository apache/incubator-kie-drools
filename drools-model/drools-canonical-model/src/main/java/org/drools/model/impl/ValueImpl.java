package org.drools.model.impl;

import org.drools.model.Value;

public class ValueImpl<T> implements Value<T> {

    private final T value;
    private final Class<T> type;

    public ValueImpl( T value ) {
        this.value = value;
        this.type = (Class<T>) value.getClass();
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
