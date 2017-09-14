package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate1<A> extends Serializable {
    boolean test(A a);
}
