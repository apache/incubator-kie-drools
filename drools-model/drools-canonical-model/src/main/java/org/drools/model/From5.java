package org.drools.model;

import org.drools.model.functions.Function5;

public interface From5<A, B, C, D, E> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Variable<E> getVariable5();
    Function5<A, B, C, D, E, ?> getProvider();
}