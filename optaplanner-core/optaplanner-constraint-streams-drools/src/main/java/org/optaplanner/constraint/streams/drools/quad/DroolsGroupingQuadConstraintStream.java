package org.optaplanner.constraint.streams.drools.quad;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.bi.DroolsAbstractBiConstraintStream;
import org.optaplanner.constraint.streams.drools.common.QuadLeftHandSide;
import org.optaplanner.constraint.streams.drools.tri.DroolsAbstractTriConstraintStream;
import org.optaplanner.constraint.streams.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

public final class DroolsGroupingQuadConstraintStream<Solution_, NewA, NewB, NewC, NewD>
        extends DroolsAbstractQuadConstraintStream<Solution_, NewA, NewB, NewC, NewD> {

    private final QuadLeftHandSide<NewA, NewB, NewC, NewD> leftHandSide;

    public <A> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyAMapping,
            UniConstraintCollector<A, ?, NewB> collectorB, UniConstraintCollector<A, ?, NewC> collectorC,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, collectorB, collectorC, collectorD);
    }

    public <A, B> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyAMapping,
            BiConstraintCollector<A, B, ?, NewB> collectorB, BiConstraintCollector<A, B, ?, NewC> collectorC,
            BiConstraintCollector<A, B, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, collectorB, collectorC, collectorD);
    }

    public <A, B, C> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriConstraintCollector<A, B, C, ?, NewB> collectorB, TriConstraintCollector<A, B, C, ?, NewC> collectorC,
            TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, collectorB, collectorC, collectorD);
    }

    public <A, B, C, D> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadFunction<A, B, C, D, NewA> groupKeyAMapping, QuadConstraintCollector<A, B, C, D, ?, NewB> collectorB,
            QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, collectorB, collectorC, collectorD);
    }

    public <A> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyAMapping,
            Function<A, NewB> groupKeyBMapping, UniConstraintCollector<A, ?, NewC> collectorC,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, collectorC, collectorD);
    }

    public <A, B> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyAMapping,
            BiFunction<A, B, NewB> groupKeyBMapping, BiConstraintCollector<A, B, ?, NewC> collectorC,
            BiConstraintCollector<A, B, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, collectorC, collectorD);
    }

    public <A, B, C> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping, TriConstraintCollector<A, B, C, ?, NewC> collectorC,
            TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, collectorC, collectorD);
    }

    public <A, B, C, D> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadFunction<A, B, C, D, NewA> groupKeyAMapping, QuadFunction<A, B, C, D, NewB> groupKeyBMapping,
            QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, collectorC, collectorD);
    }

    public <A> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyAMapping,
            Function<A, NewB> groupKeyBMapping, Function<A, NewC> groupKeyCMapping,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide =
                parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping, collectorD);
    }

    public <A, B> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyAMapping,
            BiFunction<A, B, NewB> groupKeyBMapping, BiFunction<A, B, NewC> groupKeyCMapping,
            BiConstraintCollector<A, B, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide =
                parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping, collectorD);
    }

    public <A, B, C> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping, TriFunction<A, B, C, NewC> groupKeyCMapping,
            TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide =
                parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping, collectorD);
    }

    public <A, B, C, D> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadFunction<A, B, C, D, NewA> groupKeyAMapping, QuadFunction<A, B, C, D, NewB> groupKeyBMapping,
            QuadFunction<A, B, C, D, NewC> groupKeyCMapping, QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide =
                parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping, collectorD);
    }

    public <A> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyAMapping,
            Function<A, NewB> groupKeyBMapping, Function<A, NewC> groupKeyCMapping,
            Function<A, NewD> groupKeyDMapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping,
                groupKeyDMapping);
    }

    public <A, B> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyAMapping,
            BiFunction<A, B, NewB> groupKeyBMapping, BiFunction<A, B, NewC> groupKeyCMapping,
            BiFunction<A, B, NewD> groupKeyDMapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping,
                groupKeyDMapping);
    }

    public <A, B, C> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping, TriFunction<A, B, C, NewC> groupKeyCMapping,
            TriFunction<A, B, C, NewD> groupKeyDMapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping,
                groupKeyDMapping);
    }

    public <A, B, C, D> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadFunction<A, B, C, D, NewA> groupKeyAMapping, QuadFunction<A, B, C, D, NewB> groupKeyBMapping,
            QuadFunction<A, B, C, D, NewC> groupKeyCMapping, QuadFunction<A, B, C, D, NewD> groupKeyDMapping) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping,
                groupKeyDMapping);
    }

    public <A> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, UniConstraintCollector<A, ?, NewA> collectorA,
            UniConstraintCollector<A, ?, NewB> collectorB, UniConstraintCollector<A, ?, NewC> collectorC,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(collectorA, collectorB, collectorC, collectorD);
    }

    public <A, B> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent,
            BiConstraintCollector<A, B, ?, NewA> collectorA, BiConstraintCollector<A, B, ?, NewB> collectorB,
            BiConstraintCollector<A, B, ?, NewC> collectorC, BiConstraintCollector<A, B, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(collectorA, collectorB, collectorC, collectorD);
    }

    public <A, B, C> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent,
            TriConstraintCollector<A, B, C, ?, NewA> collectorA, TriConstraintCollector<A, B, C, ?, NewB> collectorB,
            TriConstraintCollector<A, B, C, ?, NewC> collectorC,
            TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(collectorA, collectorB, collectorC, collectorD);
    }

    public <A, B, C, D> DroolsGroupingQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadConstraintCollector<A, B, C, D, ?, NewA> collectorA,
            QuadConstraintCollector<A, B, C, D, ?, NewB> collectorB,
            QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.leftHandSide = parent.getLeftHandSide().andGroupBy(collectorA, collectorB, collectorC, collectorD);
    }

    @Override
    public boolean guaranteesDistinct() {
        return true;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public QuadLeftHandSide<NewA, NewB, NewC, NewD> getLeftHandSide() {
        return leftHandSide;
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public String toString() {
        return "QuadGroupBy() with " + getChildStreams().size() + " children";
    }

}
