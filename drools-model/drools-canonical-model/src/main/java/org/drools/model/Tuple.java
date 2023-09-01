package org.drools.model;

public interface Tuple {

    Tuple getParent();
    <T> T get(Variable<T> variable);
    int size();

}
