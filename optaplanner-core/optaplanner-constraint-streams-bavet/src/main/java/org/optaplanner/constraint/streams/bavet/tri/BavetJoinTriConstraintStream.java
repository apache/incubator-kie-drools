package org.optaplanner.constraint.streams.bavet.tri;

import java.util.Objects;
import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BavetJoinBridgeBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.AbstractJoinNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.common.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;

public final class BavetJoinTriConstraintStream<Solution_, A, B, C>
        extends BavetAbstractTriConstraintStream<Solution_, A, B, C>
        implements BavetJoinConstraintStream<Solution_> {

    private final BavetJoinBridgeBiConstraintStream<Solution_, A, B> leftParent;
    private final BavetJoinBridgeUniConstraintStream<Solution_, C> rightParent;

    private final DefaultTriJoiner<A, B, C> joiner;
    private final TriPredicate<A, B, C> filtering;

    public BavetJoinTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetJoinBridgeBiConstraintStream<Solution_, A, B> leftParent,
            BavetJoinBridgeUniConstraintStream<Solution_, C> rightParent,
            DefaultTriJoiner<A, B, C> joiner, TriPredicate<A, B, C> filtering) {
        super(constraintFactory, leftParent.getRetrievalSemantics());
        this.leftParent = leftParent;
        this.rightParent = rightParent;
        this.joiner = joiner;
        this.filtering = filtering;
    }

    @Override
    public boolean guaranteesDistinct() {
        return leftParent.guaranteesDistinct() && rightParent.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        leftParent.collectActiveConstraintStreams(constraintStreamSet);
        rightParent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        int outputStoreSize = buildHelper.extractTupleStoreSize(this);
        TupleLifecycle<TriTuple<A, B, C>> downstream = buildHelper.getAggregatedTupleLifecycle(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        AbstractJoinNode<BiTuple<A, B>, C, TriTuple<A, B, C>, TriTupleImpl<A, B, C>> node = indexerFactory.hasJoiners()
                ? new IndexedJoinTriNode<>(
                        JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                        buildHelper.reserveTupleStoreIndex(leftParent.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(leftParent.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(leftParent.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(rightParent.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(rightParent.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(rightParent.getTupleSource()),
                        downstream, filtering, outputStoreSize + 2,
                        outputStoreSize, outputStoreSize + 1,
                        indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false))
                : new UnindexedJoinTriNode<>(
                        buildHelper.reserveTupleStoreIndex(leftParent.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(leftParent.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(rightParent.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(rightParent.getTupleSource()),
                        downstream, filtering, outputStoreSize + 2,
                        outputStoreSize, outputStoreSize + 1);
        buildHelper.addNode(node, leftParent, rightParent);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BavetJoinTriConstraintStream<?, ?, ?, ?> other = (BavetJoinTriConstraintStream<?, ?, ?, ?>) o;
        /*
         * Bridge streams do not implement equality because their equals() would have to point back to this stream,
         * resulting in StackOverflowError.
         * Therefore we need to check bridge parents to see where this join node comes from.
         */
        return Objects.equals(leftParent.getParent(), other.leftParent.getParent())
                && Objects.equals(rightParent.getParent(), other.rightParent.getParent())
                && Objects.equals(joiner, other.joiner)
                && Objects.equals(filtering, other.filtering);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftParent.getParent(), rightParent.getParent(), joiner, filtering);
    }

    @Override
    public String toString() {
        return "TriJoin() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
