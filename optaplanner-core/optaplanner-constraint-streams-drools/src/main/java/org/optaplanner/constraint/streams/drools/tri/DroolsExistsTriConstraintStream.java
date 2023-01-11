package org.optaplanner.constraint.streams.drools.tri;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.TriLeftHandSide;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;

public final class DroolsExistsTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, C> {

    private final DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent;
    private final Supplier<TriLeftHandSide<A, B, C>> leftHandSide;
    private final String streamName;

    public <D> DroolsExistsTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, boolean shouldExist,
            boolean shouldIncludeNullVars, Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        Predicate<D> nullityFilter = shouldIncludeNullVars ? null : constraintFactory.getNullityFilter(otherClass);
        this.leftHandSide = () -> shouldExist
                ? parent.createLeftHandSide().andExists(otherClass, joiners, nullityFilter)
                : parent.createLeftHandSide().andNotExists(otherClass, joiners, nullityFilter);
        this.streamName = shouldExist ? "TriIfExists()" : "TriIfNotExists()";
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public TriLeftHandSide<A, B, C> createLeftHandSide() {
        return leftHandSide.get();
    }

    @Override
    public String toString() {
        return streamName + " with " + getChildStreams().size() + " children";
    }

}
