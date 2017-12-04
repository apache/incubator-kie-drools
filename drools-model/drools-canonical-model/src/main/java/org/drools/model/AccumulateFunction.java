package org.drools.model;

import java.io.Serializable;
import java.util.Optional;

public interface AccumulateFunction<T, A extends Serializable, R> {

    Variable<T> getSource();

    Optional<String> getParamName();

    A init();

    void action(A acc, T obj);

    void reverse(A acc, T obj);

    R result(A acc);

    Variable<R> getVariable();
}
