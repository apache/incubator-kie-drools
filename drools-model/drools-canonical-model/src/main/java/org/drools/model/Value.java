package org.drools.model;

public interface Value<T> extends Argument<T> {

    T getValue();
}
