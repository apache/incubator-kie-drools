package org.drools.model.constraints;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.Constraint;
import org.drools.model.impl.ModelComponent;

public class AndConstraints extends AbstractConstraint implements ModelComponent {

    private final List<Constraint> constraints;

    public AndConstraints( List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public AndConstraints( Constraint... constraints) {
        this.constraints = new ArrayList<>();
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

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof AndConstraints) ) return false;

        AndConstraints that = ( AndConstraints ) o;

        return ModelComponent.areEqualInModel( constraints, that.constraints );
    }

    @Override
    public Constraint negate() {
        if (constraints.size() == 1) {
            return new AndConstraints(constraints.get(0).negate());
        }
        OrConstraints or = new OrConstraints();
        for (Constraint constraint : constraints) {
            or.or( constraint.negate() );
        }
        return or;
    }
}
