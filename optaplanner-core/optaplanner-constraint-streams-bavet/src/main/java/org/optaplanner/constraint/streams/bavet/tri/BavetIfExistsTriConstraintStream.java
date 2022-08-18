package org.optaplanner.constraint.streams.bavet.tri;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.common.quad.DefaultQuadJoiner;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class BavetIfExistsTriConstraintStream<Solution_, A, B, C, D>
        extends BavetAbstractTriConstraintStream<Solution_, A, B, C> {

    private final BavetAbstractTriConstraintStream<Solution_, A, B, C> parentABC;
    private final BavetIfExistsBridgeUniConstraintStream<Solution_, D> parentBridgeD;

    private final boolean shouldExist;
    private final DefaultQuadJoiner<A, B, C, D> joiner;
    private final QuadPredicate<A, B, C, D> filtering;

    public BavetIfExistsTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parentABC,
            BavetIfExistsBridgeUniConstraintStream<Solution_, D> parentBridgeD,
            boolean shouldExist,
            DefaultQuadJoiner<A, B, C, D> joiner, QuadPredicate<A, B, C, D> filtering) {
        super(constraintFactory, parentABC.getRetrievalSemantics());
        this.parentABC = parentABC;
        this.parentBridgeD = parentBridgeD;
        this.shouldExist = shouldExist;
        this.joiner = joiner;
        this.filtering = filtering;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parentABC.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parentABC.collectActiveConstraintStreams(constraintStreamSet);
        parentBridgeD.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parentABC.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        TupleLifecycle<TriTuple<A, B, C>> downstream = buildHelper.getAggregatedTupleLifecycle(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        AbstractIfExistsNode<TriTuple<A, B, C>, D> node = indexerFactory.hasJoiners()
                ? new IndexedIfExistsTriNode<>(shouldExist,
                        JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                        buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()),
                        downstream, indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false),
                        filtering)
                : new UnindexedIfExistsTriNode<>(shouldExist, downstream, filtering);
        buildHelper.addNode(node, this, parentBridgeD);
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
