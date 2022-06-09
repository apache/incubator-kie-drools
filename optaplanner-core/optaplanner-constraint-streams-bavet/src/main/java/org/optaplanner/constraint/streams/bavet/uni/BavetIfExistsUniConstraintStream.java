package org.optaplanner.constraint.streams.bavet.uni;

import java.util.Set;
import java.util.function.BiPredicate;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetIfExistsUniConstraintStream<Solution_, A, B> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final BavetAbstractUniConstraintStream<Solution_, A> parentA;
    private final BavetIfExistsBridgeUniConstraintStream<Solution_, B> parentBridgeB;

    private final boolean shouldExist;
    private final DefaultBiJoiner<A, B> joiner;
    private final BiPredicate<A, B> filtering;

    public BavetIfExistsUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parentA,
            BavetIfExistsBridgeUniConstraintStream<Solution_, B> parentBridgeB,
            boolean shouldExist,
            DefaultBiJoiner<A, B> joiner, BiPredicate<A, B> filtering) {
        super(constraintFactory, parentA.getRetrievalSemantics());
        this.parentA = parentA;
        this.parentBridgeB = parentBridgeB;
        this.shouldExist = shouldExist;
        this.joiner = joiner;
        this.filtering = filtering;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parentA.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parentA.collectActiveConstraintStreams(constraintStreamSet);
        parentBridgeB.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parentA.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        int inputStoreIndexA = buildHelper.reserveTupleStoreIndex(parentA.getTupleSource());
        int inputStoreIndexB = buildHelper.reserveTupleStoreIndex(parentBridgeB.getTupleSource());
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        AbstractIfExistsNode<UniTuple<A>, B> node = new IfExistsUniWithUniNode<>(shouldExist,
                JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                inputStoreIndexA, inputStoreIndexB,
                buildHelper.getAggregatedTupleLifecycle(childStreamList),
                indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false),
                filtering);
        buildHelper.addNode(node, this, parentBridgeB);
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
