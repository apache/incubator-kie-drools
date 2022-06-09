package org.optaplanner.constraint.streams.drools.uni;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.bi.DroolsAbstractBiConstraintStream;
import org.optaplanner.constraint.streams.drools.common.UniLeftHandSide;
import org.optaplanner.constraint.streams.drools.quad.DroolsAbstractQuadConstraintStream;
import org.optaplanner.constraint.streams.drools.tri.DroolsAbstractTriConstraintStream;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;

public final class DroolsMappingUniConstraintStream<Solution_, NewA>
        extends DroolsAbstractUniConstraintStream<Solution_, NewA> {

    private final UniLeftHandSide<NewA> leftHandSide;

    public <A> DroolsMappingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> mapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andMap(mapping);
    }

    public <A, B> DroolsMappingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> mapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andMap(mapping);
    }

    public <A, B, C> DroolsMappingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> mapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andMap(mapping);
    }

    public <A, B, C, D> DroolsMappingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent, QuadFunction<A, B, C, D, NewA> mapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andMap(mapping);
    }

    @Override
    public boolean guaranteesDistinct() {
        return false; // map() can never guarantee distinct tuples, as we do not see inside of the mapping function.
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
        return "Map() with " + getChildStreams().size() + " children";
    }

}
