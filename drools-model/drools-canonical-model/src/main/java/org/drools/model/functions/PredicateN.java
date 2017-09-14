package org.drools.model.functions;

public interface PredicateN {
    boolean test(Object... objs);

    PredicateN True = objs -> true;
}
