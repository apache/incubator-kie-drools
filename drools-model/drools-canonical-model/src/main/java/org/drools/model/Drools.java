package org.drools.model;

public interface Drools extends DroolsEntryPoint {

    <T> T getRuntime(Class<T> runtimeClass);

    <T> T getContext(Class<T> contextClass);

    DroolsEntryPoint getEntryPoint(String name);

    void halt();

    void setFocus(String focus);

    Channel getChannel(String name);

    void logicalInsert(Object object);

    void insertAsync(Object object);
}
