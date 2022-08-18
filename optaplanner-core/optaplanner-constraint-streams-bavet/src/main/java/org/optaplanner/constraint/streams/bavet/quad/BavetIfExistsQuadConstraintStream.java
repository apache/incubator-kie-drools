package org.optaplanner.constraint.streams.bavet.quad;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.common.penta.DefaultPentaJoiner;
import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class BavetIfExistsQuadConstraintStream<Solution_, A, B, C, D, E>
        extends BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parentABCD;
    private final BavetIfExistsBridgeUniConstraintStream<Solution_, E> parentBridgeE;

    private final boolean shouldExist;
    private final DefaultPentaJoiner<A, B, C, D, E> joiner;
    private final PentaPredicate<A, B, C, D, E> filtering;

    public BavetIfExistsQuadConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parentABCD,
            BavetIfExistsBridgeUniConstraintStream<Solution_, E> parentBridgeE,
            boolean shouldExist,
            DefaultPentaJoiner<A, B, C, D, E> joiner, PentaPredicate<A, B, C, D, E> filtering) {
        super(constraintFactory, parentABCD.getRetrievalSemantics());
        this.parentABCD = parentABCD;
        this.parentBridgeE = parentBridgeE;
        this.shouldExist = shouldExist;
        this.joiner = joiner;
        this.filtering = filtering;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parentABCD.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parentABCD.collectActiveConstraintStreams(constraintStreamSet);
        parentBridgeE.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parentABCD.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        TupleLifecycle<QuadTuple<A, B, C, D>> downstream = buildHelper.getAggregatedTupleLifecycle(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        AbstractIfExistsNode<QuadTuple<A, B, C, D>, E> node = indexerFactory.hasJoiners()
                ? new IndexedIfExistsQuadNode<>(shouldExist,
                        JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                        buildHelper.reserveTupleStoreIndex(parentABCD.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeE.getTupleSource()),
                        downstream, indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false),
                        filtering)
                : new UnindexedIfExistsQuadNode<>(shouldExist, downstream, filtering);
        buildHelper.addNode(node, this, parentBridgeE);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // TODO

    @Override
    public String toString() {
        return "IfExists() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
