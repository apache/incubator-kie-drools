package org.drools.model.functions;

import java.io.Serializable;

public interface PredicateN extends Serializable {
    boolean test(Object... objs) throws Exception;

    default PredicateN negate() {
        return a -> !test( a );
    }

    PredicateN True = objs -> true;
    PredicateN False = objs -> false;
}
