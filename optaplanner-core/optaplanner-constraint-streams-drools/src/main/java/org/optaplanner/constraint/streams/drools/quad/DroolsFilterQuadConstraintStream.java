package org.optaplanner.constraint.streams.drools.quad;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.QuadLeftHandSide;
import org.optaplanner.core.api.function.QuadPredicate;

public final class DroolsFilterQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent;
    private final QuadLeftHandSide<A, B, C, D> leftHandSide;

    public DroolsFilterQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent, QuadPredicate<A, B, C, D> predicate) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.leftHandSide = parent.getLeftHandSide().andFilter(predicate);
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public QuadLeftHandSide<A, B, C, D> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public String toString() {
        return "QuadFilter() with " + getChildStreams().size() + " children";
    }

}
