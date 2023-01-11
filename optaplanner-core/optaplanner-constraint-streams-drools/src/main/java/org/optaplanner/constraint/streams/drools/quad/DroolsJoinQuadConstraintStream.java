package org.optaplanner.constraint.streams.drools.quad;

import java.util.function.Supplier;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.QuadLeftHandSide;
import org.optaplanner.constraint.streams.drools.tri.DroolsAbstractTriConstraintStream;
import org.optaplanner.constraint.streams.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;

public final class DroolsJoinQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final Supplier<QuadLeftHandSide<A, B, C, D>> leftHandSide;
    private final boolean guaranteesDistinct;

    public DroolsJoinQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent,
            DroolsAbstractUniConstraintStream<Solution_, D> otherStream, QuadJoiner<A, B, C, D> joiner) {
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
    public QuadLeftHandSide<A, B, C, D> createLeftHandSide() {
        return leftHandSide.get();
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public String toString() {
        return "QuadJoin() with " + getChildStreams().size() + " children";
    }

}
