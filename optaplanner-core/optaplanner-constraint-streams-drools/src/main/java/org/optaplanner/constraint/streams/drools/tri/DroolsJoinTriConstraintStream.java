package org.optaplanner.constraint.streams.drools.tri;

import java.util.function.Supplier;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.bi.DroolsAbstractBiConstraintStream;
import org.optaplanner.constraint.streams.drools.common.TriLeftHandSide;
import org.optaplanner.constraint.streams.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;

public final class DroolsJoinTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, C> {

    private final Supplier<TriLeftHandSide<A, B, C>> leftHandSide;
    private final boolean guaranteesDistinct;

    public DroolsJoinTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent,
            DroolsAbstractUniConstraintStream<Solution_, C> otherStream, TriJoiner<A, B, C> joiner) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = () -> parent.createLeftHandSide().andJoin(otherStream.createLeftHandSide(), joiner);
        this.guaranteesDistinct = parent.guaranteesDistinct() && otherStream.guaranteesDistinct();
    }

    @Override
    public boolean guaranteesDistinct() {
        return guaranteesDistinct;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public TriLeftHandSide<A, B, C> createLeftHandSide() {
        return leftHandSide.get();
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public String toString() {
        return "TriJoin() with " + getChildStreams().size() + " children";
    }

}
