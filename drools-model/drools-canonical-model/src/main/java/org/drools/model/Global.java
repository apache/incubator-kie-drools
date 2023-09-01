package org.drools.model;

public interface Global<T> extends Variable<T>, NamedModelItem {

    @Override
    default boolean isFact() {
        return false;
    }
}
