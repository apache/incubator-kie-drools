package org.drools.model;

public class TypeReference<T> {

    public final Class<?> rawType;

    public TypeReference(Class<?> rawType) {
        this.rawType = rawType;
    }

    public Class<T> getType() {
        return (Class<T>) rawType;
    }
}
