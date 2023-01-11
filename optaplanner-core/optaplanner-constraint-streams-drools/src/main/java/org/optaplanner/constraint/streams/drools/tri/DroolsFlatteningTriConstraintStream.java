package org.optaplanner.constraint.streams.drools.tri;

import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.TriLeftHandSide;

public final class DroolsFlatteningTriConstraintStream<Solution_, A, B, NewC>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, NewC> {

    private final Supplier<TriLeftHandSide<A, B, NewC>> leftHandSide;

    public <C> DroolsFlatteningTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, Function<C, Iterable<NewC>> triMapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = () -> parent.createLeftHandSide().andFlattenLast(triMapping);
    }

    @Override
    public boolean guaranteesDistinct() {
        return false; // flattening can never guarantee distinct tuples, as we do not see inside the Iterable.
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public TriLeftHandSide<A, B, NewC> createLeftHandSide() {
        return leftHandSide.get();
    }

    @Override
    public String toString() {
        return "TriFlatten() with " + getChildStreams().size() + " children";
    }

}
