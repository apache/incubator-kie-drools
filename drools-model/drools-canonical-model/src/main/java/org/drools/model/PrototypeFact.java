package org.drools.model;

public interface PrototypeFact {

    boolean has(String name);

    Object get(String name);

    void set(String name, Object value);
}
