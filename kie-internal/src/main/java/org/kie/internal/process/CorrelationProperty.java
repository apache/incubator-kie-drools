package org.kie.internal.process;

public interface CorrelationProperty<T> {

    String getName();

    String getType();

    T getValue();
}
