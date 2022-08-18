package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Map;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIndexedJoinNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class IndexedJoinBiNode<A, B> extends AbstractIndexedJoinNode<UniTuple<A>, B, BiTuple<A, B>, BiTupleImpl<A, B>> {

    private final Function<A, IndexProperties> mappingA;
    private final int outputStoreSize;

    public IndexedJoinBiNode(Function<A, IndexProperties> mappingA, Function<B, IndexProperties> mappingB,
            int inputStoreIndexA, int inputStoreIndexB,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle,
            int outputStoreSize,
            Indexer<UniTuple<A>, Map<UniTuple<B>, BiTupleImpl<A, B>>> indexerA,
            Indexer<UniTuple<B>, Map<UniTuple<A>, BiTupleImpl<A, B>>> indexerB) {
        super(mappingB, inputStoreIndexA, inputStoreIndexB, nextNodesTupleLifecycle, indexerA, indexerB);
        this.mappingA = mappingA;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected IndexProperties createIndexPropertiesLeft(UniTuple<A> leftTuple) {
        return mappingA.apply(leftTuple.getFactA());
    }

    @Override
    protected BiTupleImpl<A, B> createOutTuple(UniTuple<A> leftTuple, UniTuple<B> rightTuple) {
        return new BiTupleImpl<>(leftTuple.getFactA(), rightTuple.getFactA(), outputStoreSize);
    }

    @Override
    protected void updateOutTupleLeft(BiTupleImpl<A, B> outTuple, UniTuple<A> leftTuple) {
        outTuple.factA = leftTuple.getFactA();
    }

    @Override
    protected void updateOutTupleRight(BiTupleImpl<A, B> outTuple, UniTuple<B> rightTuple) {
        outTuple.factB = rightTuple.getFactA();
    }

    @Override
    public String toString() {
        return "JoinBiNode";
    }

}
