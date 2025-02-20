package org.drools.model;

import org.drools.model.functions.Function6;

public interface From6<A, B, C, D, E, F> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Variable<E> getVariable5();
    Variable<F> getVariable6();
    Function6<A, B, C, D, E, F, ?> getProvider();
}