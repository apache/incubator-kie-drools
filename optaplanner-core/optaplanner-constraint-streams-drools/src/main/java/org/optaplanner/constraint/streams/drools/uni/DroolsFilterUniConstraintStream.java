package org.optaplanner.constraint.streams.drools.uni;

import java.util.function.Predicate;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.UniLeftHandSide;

public final class DroolsFilterUniConstraintStream<Solution_, A> extends DroolsAbstractUniConstraintStream<Solution_, A> {

    private final DroolsAbstractUniConstraintStream<Solution_, A> parent;
    private final UniLeftHandSide<A> leftHandSide;

    public DroolsFilterUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Predicate<A> predicate) {
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
    public UniLeftHandSide<A> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public String toString() {
        return "Filter() with " + getChildStreams().size() + " children";
    }

}
