package org.drools.model;

import java.util.List;

import org.drools.model.constraints.AbstractSingleConstraint;
import org.drools.model.constraints.AndConstraints;
import org.drools.model.functions.PredicateN;

public interface Constraint {
    enum Type { SINGLE, OR, AND }

    List<Constraint> getChildren();

    Type getType();

    Constraint EMPTY = new AbstractSingleConstraint("EMPTY") {
        @Override
        public Variable[] getVariables() {
            return new Variable[0];
        }

        @Override
        public PredicateN getPredicate() {
            return PredicateN.True;
        }

        @Override
        public AndConstraints and( Constraint constraint ) {
            return new AndConstraints(constraint);
        }
    };
}
