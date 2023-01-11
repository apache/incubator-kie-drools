package org.optaplanner.constraint.streams.drools.uni;

import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.UniLeftHandSide;

public final class DroolsFromUniConstraintStream<Solution_, A> extends DroolsAbstractUniConstraintStream<Solution_, A> {

    private final Class<A> fromClass;

    public DroolsFromUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory, Class<A> fromClass,
            RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
        this.fromClass = fromClass;
    }

    @Override
    public boolean guaranteesDistinct() {
        return true;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public UniLeftHandSide<A> createLeftHandSide() {
        return new UniLeftHandSide<>(fromClass, constraintFactory.getVariableFactory());
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public String toString() {
        return "From(" + fromClass.getSimpleName() + ") with " + getChildStreams().size() + " children";
    }

}
