package org.optaplanner.constraint.streams.drools.bi;

import java.util.function.BiPredicate;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.BiLeftHandSide;

public final class DroolsFilterBiConstraintStream<Solution_, A, B>
        extends DroolsAbstractBiConstraintStream<Solution_, A, B> {

    private final DroolsAbstractBiConstraintStream<Solution_, A, B> parent;
    private final BiLeftHandSide<A, B> leftHandSide;

    public DroolsFilterBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiPredicate<A, B> biPredicate) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.leftHandSide = parent.getLeftHandSide().andFilter(biPredicate);
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    @Override
    public BiLeftHandSide<A, B> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public String toString() {
        return "BiFilter() with " + getChildStreams().size() + " children";
    }

}
