package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.common.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetIfExistsBiConstraintStream<Solution_, A, B, C>
        extends BavetAbstractBiConstraintStream<Solution_, A, B> {

    private final BavetAbstractBiConstraintStream<Solution_, A, B> parentAB;
    private final BavetIfExistsBridgeUniConstraintStream<Solution_, C> parentBridgeC;

    private final boolean shouldExist;
    private final DefaultTriJoiner<A, B, C> joiner;
    private final TriPredicate<A, B, C> filtering;

    public BavetIfExistsBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parentAB,
            BavetIfExistsBridgeUniConstraintStream<Solution_, C> parentBridgeC,
            boolean shouldExist,
            DefaultTriJoiner<A, B, C> joiner, TriPredicate<A, B, C> filtering) {
        super(constraintFactory, parentAB.getRetrievalSemantics());
        this.parentAB = parentAB;
        this.parentBridgeC = parentBridgeC;
        this.shouldExist = shouldExist;
        this.joiner = joiner;
        this.filtering = filtering;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parentAB.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parentAB.collectActiveConstraintStreams(constraintStreamSet);
        parentBridgeC.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parentAB.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        TupleLifecycle<BiTuple<A, B>> downstream = buildHelper.getAggregatedTupleLifecycle(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        AbstractIfExistsNode<BiTuple<A, B>, C> node = indexerFactory.hasJoiners()
                ? new IndexedIfExistsBiNode<>(shouldExist,
                        JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                        buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()),
                        downstream, indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false),
                        filtering)
                : new UnindexedIfExistsBiNode<>(shouldExist, downstream, filtering);
        buildHelper.addNode(node, this, parentBridgeC);
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
