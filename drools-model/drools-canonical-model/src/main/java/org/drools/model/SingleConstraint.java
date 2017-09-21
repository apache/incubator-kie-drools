package org.drools.model;

import org.drools.model.functions.PredicateN;

public interface SingleConstraint extends Constraint {
    Variable[] getVariables();

    PredicateN getPredicate();

    Index getIndex();

    String getExprId();

    String[] getReactiveProps();

    default boolean isTemporal() {
        return false;
    }

    @Override
    default Type getType() {
        return Type.SINGLE;
    }
}
