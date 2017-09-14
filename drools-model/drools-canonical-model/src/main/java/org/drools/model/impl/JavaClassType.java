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
}
