package org.drools.model;

public interface Drools {
    void insert(Object object);

    void insertLogical(Object object);

    void update(Object object, String... modifiedProperties);

    void update(Object object, BitMask modifiedProperties);

    void delete(Object object);

    <T> T getRuntime(Class<T> runtimeClass);
}