package org.drools.model;

public interface Drools extends DroolsEntryPoint {

    void insertLogical(Object object);

    <T> T getRuntime(Class<T> runtimeClass);

    DroolsEntryPoint getEntryPoint(String name);
}