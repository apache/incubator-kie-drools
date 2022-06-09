package org.optaplanner.constraint.streams.drools.bi;

import java.util.function.Function;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.BiLeftHandSide;

public final class DroolsFlatteningBiConstraintStream<Solution_, A, NewB>
        extends DroolsAbstractBiConstraintStream<Solution_, A, NewB> {

    private final BiLeftHandSide<A, NewB> leftHandSide;

    public <B> DroolsFlatteningBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, Function<B, Iterable<NewB>> biMapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andFlattenLast(biMapping);
    }

    @Override
    public boolean guaranteesDistinct() {
        return false; // flattening can never guarantee distinct tuples, as we do not see inside the Iterable.
    }

    @Override
    public BiLeftHandSide<A, NewB> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public String toString() {
        return "BiFlatten() with " + getChildStreams().size() + " children";
    }

}
