package org.drools.model;

import org.drools.model.functions.Function10;

public interface From10<A, B, C, D, E, F, G, H, I, J> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Variable<E> getVariable5();
    Variable<F> getVariable6();
    Variable<G> getVariable7();
    Variable<H> getVariable8();
    Variable<I> getVariable9();
    Variable<J> getVariable10();
    Function10<A, B, C, D, E, F, G, H, I, J, ?> getProvider();
}