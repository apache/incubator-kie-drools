package org.optaplanner.constraint.streams.drools.uni;

import java.util.function.Function;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.UniLeftHandSide;

public final class DroolsFlatteningUniConstraintStream<Solution_, NewA>
        extends DroolsAbstractUniConstraintStream<Solution_, NewA> {

    private final UniLeftHandSide<NewA> leftHandSide;

    public <A> DroolsFlatteningUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, Iterable<NewA>> mapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andFlattenLast(mapping);
    }

    @Override
    public boolean guaranteesDistinct() {
        return false; // flattening can never guarantee distinct tuples, as we do not see inside the Iterable.
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public UniLeftHandSide<NewA> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public String toString() {
        return "Flatten() with " + getChildStreams().size() + " children";
    }

}
