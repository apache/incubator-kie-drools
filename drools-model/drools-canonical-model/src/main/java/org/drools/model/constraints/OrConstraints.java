package org.drools.model.constraints;

import org.drools.model.Constraint;

import java.util.ArrayList;
import java.util.List;

public class OrConstraints extends AbstractConstraint {

    private final List<Constraint> constraints = new ArrayList<Constraint>();

    OrConstraints(Constraint... constraints) {
        for (Constraint constraint : constraints) {
            or(constraint);
        }
    }

    @Override
    public OrConstraints or(Constraint constraint) {
        constraints.add(constraint);
        return this;
    }

    @Override
    public List<Constraint> getChildren() {
        return constraints;
    }

    @Override
    public Type getType() {
        return Type.OR;
    }
}
