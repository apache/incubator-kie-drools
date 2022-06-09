package org.optaplanner.constraint.streams.drools.quad;

import java.util.function.Predicate;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.QuadLeftHandSide;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;

public final class DroolsExistsQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent;
    private final QuadLeftHandSide<A, B, C, D> leftHandSide;
    private final String streamName;

    public <E> DroolsExistsQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent, boolean shouldExist,
            boolean shouldIncludeNullVars, Class<E> otherClass, PentaJoiner<A, B, C, D, E>... joiners) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        Predicate<E> nullityFilter = shouldIncludeNullVars ? null : constraintFactory.getNullityFilter(otherClass);
        this.leftHandSide = shouldExist
                ? parent.getLeftHandSide().andExists(otherClass, joiners, nullityFilter)
                : parent.getLeftHandSide().andNotExists(otherClass, joiners, nullityFilter);
        this.streamName = shouldExist ? "QuadIfExists()" : "QuadIfNotExists()";
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
        return streamName + " with " + getChildStreams().size() + " children";
    }

}
