package org.drools.model;

import org.drools.model.functions.Function9;

public interface From9<A, B, C, D, E, F, G, H, I> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Variable<E> getVariable5();
    Variable<F> getVariable6();
    Variable<G> getVariable7();
    Variable<H> getVariable8();
    Variable<I> getVariable9();
    Function9<A, B, C, D, E, F, G, H, I, ?> getProvider();
}