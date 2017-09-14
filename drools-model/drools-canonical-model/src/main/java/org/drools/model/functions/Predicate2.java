package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate2<A, B> extends Serializable {
    boolean test(A a, B b);
}
