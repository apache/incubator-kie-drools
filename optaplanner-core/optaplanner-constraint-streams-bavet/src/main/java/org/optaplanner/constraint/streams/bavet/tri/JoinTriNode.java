package org.optaplanner.constraint.streams.bavet.tri;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.AbstractJoinNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class JoinTriNode<A, B, C> extends AbstractJoinNode<BiTuple<A, B>, C, TriTuple<A, B, C>> {

    private final BiFunction<A, B, IndexProperties> mappingAB;
    private final int outputStoreSize;

    public JoinTriNode(BiFunction<A, B, IndexProperties> mappingAB, Function<C, IndexProperties> mappingC,
            int inputStoreIndexAB, int inputStoreIndexC,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle,
            int outputStoreSize,
            Indexer<BiTuple<A, B>, Map<UniTuple<C>, TriTuple<A, B, C>>> indexerAB,
            Indexer<UniTuple<C>, Map<BiTuple<A, B>, TriTuple<A, B, C>>> indexerC) {
        super(mappingC, inputStoreIndexAB, inputStoreIndexC, nextNodesTupleLifecycle, indexerAB, indexerC);
        this.mappingAB = mappingAB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected IndexProperties createIndexPropertiesLeft(BiTuple<A, B> leftTuple) {
        return mappingAB.apply(leftTuple.factA, leftTuple.factB);
    }

    @Override
    protected TriTuple<A, B, C> createOutTuple(BiTuple<A, B> leftTuple, UniTuple<C> rightTuple) {
        return new TriTuple<>(leftTuple.factA, leftTuple.factB, rightTuple.factA, outputStoreSize);
    }

    @Override
    protected void updateOutTupleLeft(TriTuple<A, B, C> outTuple, BiTuple<A, B> leftTuple) {
        outTuple.factA = leftTuple.factA;
        outTuple.factB = leftTuple.factB;
    }

    @Override
    protected void updateOutTupleRight(TriTuple<A, B, C> outTuple, UniTuple<C> rightTuple) {
        outTuple.factC = rightTuple.factA;
    }

    @Override
    public String toString() {
        return "JoinTriNode";
    }

}
