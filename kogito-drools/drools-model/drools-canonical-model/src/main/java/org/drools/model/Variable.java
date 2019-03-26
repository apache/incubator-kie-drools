package org.drools.model;

public interface Variable<T> extends Argument<T> {

    String getName();

    default boolean isFact() {
        return true;
    }
}
