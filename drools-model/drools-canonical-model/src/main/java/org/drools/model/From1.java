package org.drools.model;

import org.drools.model.functions.Function1;

public interface From1<A> extends From<A> {
    Function1<A,?> getProvider();
}
