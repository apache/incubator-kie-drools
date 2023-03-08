package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractJoinNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.core.api.score.Score;

public final class BavetJoinBiConstraintStream<Solution_, A, B> extends BavetAbstractBiConstraintStream<Solution_, A, B>
        implements BavetJoinConstraintStream<Solution_> {

    private final BavetJoinBridgeUniConstraintStream<Solution_, A> leftParent;
    private final BavetJoinBridgeUniConstraintStream<Solution_, B> rightParent;
    private final DefaultBiJoiner<A, B> joiner;
    private final BiPredicate<A, B> filtering;

    public BavetJoinBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetJoinBridgeUniConstraintStream<Solution_, A> leftParent,
            BavetJoinBridgeUniConstraintStream<Solution_, B> rightParent,
            DefaultBiJoiner<A, B> joiner, BiPredicate<A, B> filtering) {
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
        TupleLifecycle<BiTuple<A, B>> downstream = buildHelper.getAggregatedTupleLifecycle(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        AbstractJoinNode<UniTuple<A>, B, BiTuple<A, B>, BiTupleImpl<A, B>> node = indexerFactory.hasJoiners()
                ? new IndexedJoinBiNode<>(
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
                : new UnindexedJoinBiNode<>(
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
        BavetJoinBiConstraintStream<?, ?, ?> other = (BavetJoinBiConstraintStream<?, ?, ?>) o;
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
        return "BiJoin() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
