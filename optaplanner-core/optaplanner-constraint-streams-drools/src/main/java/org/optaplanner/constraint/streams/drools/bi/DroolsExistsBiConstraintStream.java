package org.optaplanner.constraint.streams.drools.bi;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.BiLeftHandSide;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;

public final class DroolsExistsBiConstraintStream<Solution_, A, B>
        extends DroolsAbstractBiConstraintStream<Solution_, A, B> {

    private final DroolsAbstractBiConstraintStream<Solution_, A, B> parent;
    private final Supplier<BiLeftHandSide<A, B>> leftHandSide;
    private final String streamName;

    public <C> DroolsExistsBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, boolean shouldExist,
            boolean shouldIncludeNullVars, Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        Predicate<C> nullityFilter = shouldIncludeNullVars ? null : constraintFactory.getNullityFilter(otherClass);
        this.leftHandSide = () -> shouldExist
                ? parent.createLeftHandSide().andExists(otherClass, joiners, nullityFilter)
                : parent.createLeftHandSide().andNotExists(otherClass, joiners, nullityFilter);
        this.streamName = shouldExist ? "BiIfExists()" : "BiIfNotExists()";
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public BiLeftHandSide<A, B> createLeftHandSide() {
        return leftHandSide.get();
    }

    @Override
    public String toString() {
        return streamName + " with " + getChildStreams().size() + " children";
    }

}
