package org.drools.model.constraints;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.Constraint;
import org.drools.model.impl.ModelComponent;

public class MultipleConstraints extends AbstractConstraint implements ModelComponent {

    private final List<Constraint> constraints;

    public MultipleConstraints( List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public MultipleConstraints( Constraint... constraints) {
        this.constraints = new ArrayList<>();
        for (Constraint constraint : constraints) {
            with(constraint);
        }
    }

    @Override
    public MultipleConstraints with( Constraint constraint) {
        constraints.add(constraint);
        return this;
    }

    @Override
    public List<Constraint> getChildren() {
        return constraints;
    }

    @Override
    public Type getType() {
        return Type.MULTIPLE;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof MultipleConstraints) ) return false;

        MultipleConstraints that = ( MultipleConstraints ) o;

        return ModelComponent.areEqualInModel( constraints, that.constraints );
    }
}
