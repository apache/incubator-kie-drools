package org.drools.model;

import org.drools.model.functions.Function7;

public interface From7<A, B, C, D, E, F, G> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Variable<E> getVariable5();
    Variable<F> getVariable6();
    Variable<G> getVariable7();
    Function7<A, B, C, D, E, F, G, ?> getProvider();
}