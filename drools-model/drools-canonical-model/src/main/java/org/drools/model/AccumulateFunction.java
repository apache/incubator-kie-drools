package org.drools.model;

import java.io.Serializable;

public interface AccumulateFunction<T, A extends Serializable, R> {

    A init();

    void action(A acc, T obj);

    void reverse(A acc, T obj);

    R result(A acc);

    Variable<R> getVariable();
}
