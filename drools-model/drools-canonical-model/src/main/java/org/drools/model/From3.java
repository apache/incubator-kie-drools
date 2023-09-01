package org.drools.model;

import org.drools.model.functions.Function3;

public interface From3<A, B, C> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Function3<A,B,C,?> getProvider();
}
