package org.drools.model.constraints;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.Constraint;
import org.drools.model.impl.ModelComponent;

public class OrConstraints extends AbstractConstraint implements ModelComponent {

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

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof OrConstraints) ) return false;

        OrConstraints that = ( OrConstraints ) o;

        return ModelComponent.areEqualInModel( constraints, that.constraints );
    }
}
