package org.drools.model;

import org.drools.model.functions.Function4;

public interface From4<A, B, C, D> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Function4<A,B,C,D,?> getProvider();
}
