package org.drools.model;

public interface Type<T> {
    boolean isInstance(Object obj);
    Class<T> asClass();
}
