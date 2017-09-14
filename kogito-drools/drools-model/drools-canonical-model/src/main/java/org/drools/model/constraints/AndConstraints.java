package org.drools.model.constraints;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.Constraint;

public class AndConstraints extends AbstractConstraint {

    private final List<Constraint> constraints = new ArrayList<Constraint>();

    public AndConstraints(Constraint... constraints) {
        for (Constraint constraint : constraints) {
            and(constraint);
        }
    }

    @Override
    public AndConstraints and(Constraint constraint) {
        constraints.add(constraint);
        return this;
    }

    @Override
    public List<Constraint> getChildren() {
        return constraints;
    }

    @Override
    public Type getType() {
        return Type.AND;
    }
}
