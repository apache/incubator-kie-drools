package org.drools.model.functions;

import java.io.Serializable;

public interface PredicateN extends Serializable {
    boolean test(Object... objs);

    PredicateN True = objs -> true;
}
