package org.drools.model;

import org.drools.model.functions.Function8;

public interface From8<A, B, C, D, E, F, G, H> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Variable<E> getVariable5();
    Variable<F> getVariable6();
    Variable<G> getVariable7();
    Variable<H> getVariable8();
    Function8<A, B, C, D, E, F, G, H, ?> getProvider();
}