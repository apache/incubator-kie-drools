package org.drools.model;

import org.drools.model.functions.Function2;

public interface From2<A, B> extends From<A> {
    Variable<B> getVariable2();
    Function2<A,B,?> getProvider();
}
