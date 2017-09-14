package org.drools.model.constraints;

import org.drools.model.Constraint;

public abstract class AbstractConstraint implements Constraint {

    public OrConstraints or(Constraint constraint) {
        return new OrConstraints(this, constraint);
    }

    public AndConstraints and(Constraint constraint) {
        return new AndConstraints(this, constraint);
    }

    public static AndConstraints and(Constraint... constraints) {
        return new AndConstraints(constraints);
    }

    public static OrConstraints or(Constraint... constraints) {
        return new OrConstraints(constraints);
    }
}