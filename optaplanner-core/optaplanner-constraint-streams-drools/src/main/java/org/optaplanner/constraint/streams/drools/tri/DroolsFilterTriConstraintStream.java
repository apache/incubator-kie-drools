package org.optaplanner.constraint.streams.drools.tri;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.TriLeftHandSide;
import org.optaplanner.core.api.function.TriPredicate;

public final class DroolsFilterTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, C> {

    private final DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent;
    private final TriLeftHandSide<A, B, C> leftHandSide;

    public DroolsFilterTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriPredicate<A, B, C> triPredicate) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.leftHandSide = parent.getLeftHandSide().andFilter(triPredicate);
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public TriLeftHandSide<A, B, C> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public String toString() {
        return "TriFilter() with " + getChildStreams().size() + " children";
    }

}
