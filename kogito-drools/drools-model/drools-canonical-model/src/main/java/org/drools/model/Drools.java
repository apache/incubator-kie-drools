package org.drools.model;

public interface Drools extends DroolsEntryPoint {

    <T> T getRuntime(Class<T> runtimeClass);

    DroolsEntryPoint getEntryPoint(String name);

    void halt();

    void setFocus(String focus);
}