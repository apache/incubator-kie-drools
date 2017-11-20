package org.drools.model;

import org.drools.model.constraints.AbstractSingleConstraint;
import org.drools.model.constraints.AndConstraints;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;

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

    SingleConstraint EMPTY = new AbstractSingleConstraint("EMPTY") {
        @Override
        public Variable[] getVariables() {
            return new Variable[0];
        }

        @Override
        public PredicateN getPredicate() {
            return PredicateN.True;
        }

        @Override
        public AndConstraints and(Constraint constraint ) {
            return new AndConstraints(constraint);
        }

        @Override
        public boolean isEqualTo( ModelComponent other ) {
            return this == other;
        }
    };
}
