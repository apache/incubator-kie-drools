package org.drools.model;

import org.drools.model.functions.Function11;

public interface From11<A, B, C, D, E, F, G, H, I, J, K> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Variable<E> getVariable5();
    Variable<F> getVariable6();
    Variable<G> getVariable7();
    Variable<H> getVariable8();
    Variable<I> getVariable9();
    Variable<J> getVariable10();
    Variable<K> getVariable11();
    Function11<A, B, C, D, E, F, G, H, I, J, K, ?> getProvider();
}