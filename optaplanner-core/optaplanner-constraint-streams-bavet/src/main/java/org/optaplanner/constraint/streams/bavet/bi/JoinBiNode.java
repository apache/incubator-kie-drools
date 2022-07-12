package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Map;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractJoinNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class JoinBiNode<A, B> extends AbstractJoinNode<UniTuple<A>, B, BiTuple<A, B>> {

    private final Function<A, IndexProperties> mappingA;
    private final int outputStoreSize;

    public JoinBiNode(Function<A, IndexProperties> mappingA, Function<B, IndexProperties> mappingB,
            int inputStoreIndexA, int inputStoreIndexB,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle,
            int outputStoreSize,
            Indexer<UniTuple<A>, Map<UniTuple<B>, BiTuple<A, B>>> indexerA,
            Indexer<UniTuple<B>, Map<UniTuple<A>, BiTuple<A, B>>> indexerB) {
        super(mappingB, inputStoreIndexA, inputStoreIndexB, nextNodesTupleLifecycle, indexerA, indexerB);
        this.mappingA = mappingA;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected IndexProperties createIndexPropertiesLeft(UniTuple<A> leftTuple) {
        return mappingA.apply(leftTuple.factA);
    }

    @Override
    protected BiTuple<A, B> createOutTuple(UniTuple<A> leftTuple, UniTuple<B> rightTuple) {
        return new BiTuple<>(leftTuple.factA, rightTuple.factA, outputStoreSize);
    }

    @Override
    protected void updateOutTupleLeft(BiTuple<A, B> outTuple, UniTuple<A> leftTuple) {
        outTuple.factA = leftTuple.factA;
    }

    @Override
    protected void updateOutTupleRight(BiTuple<A, B> outTuple, UniTuple<B> rightTuple) {
        outTuple.factB = rightTuple.factA;
    }

    @Override
    public String toString() {
        return "JoinBiNode";
    }

}
