package org.drools.model.constraints;

import org.drools.model.Constraint;

public abstract class AbstractConstraint implements Constraint {

    public OrConstraints or(Constraint constraint) {
        return new OrConstraints(this, constraint);
    }

    public MultipleConstraints with(Constraint constraint) {
        return new MultipleConstraints(this, constraint);
    }

    public AndConstraints and(Constraint constraint) {
        return new AndConstraints(this, constraint);
    }
}