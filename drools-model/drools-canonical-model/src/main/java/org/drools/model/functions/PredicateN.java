package org.drools.model.functions;

import java.io.Serializable;

public interface PredicateN extends Serializable {
    boolean test(Object... objs) throws Exception;

    PredicateN True = objs -> true;
    PredicateN False = objs -> false;
}
