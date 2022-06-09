package org.optaplanner.constraint.streams.bavet.quad;

import java.util.Map;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractJoinNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.TriFunction;

final class JoinQuadNode<A, B, C, D> extends AbstractJoinNode<TriTuple<A, B, C>, D, QuadTuple<A, B, C, D>> {

    private final TriFunction<A, B, C, IndexProperties> mappingABC;
    private final int outputStoreSize;

    public JoinQuadNode(TriFunction<A, B, C, IndexProperties> mappingABC, Function<D, IndexProperties> mappingD,
            int inputStoreIndexAB, int inputStoreIndexC,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle,
            int outputStoreSize,
            Indexer<TriTuple<A, B, C>, Map<UniTuple<D>, QuadTuple<A, B, C, D>>> indexerABC,
            Indexer<UniTuple<D>, Map<TriTuple<A, B, C>, QuadTuple<A, B, C, D>>> indexerD) {
        super(mappingD, inputStoreIndexAB, inputStoreIndexC, nextNodesTupleLifecycle, indexerABC, indexerD);
        this.mappingABC = mappingABC;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected IndexProperties createIndexProperties(TriTuple<A, B, C> tuple) {
        return mappingABC.apply(tuple.factA, tuple.factB, tuple.factC);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(TriTuple<A, B, C> leftTuple, UniTuple<D> rightTuple) {
        return new QuadTuple<>(leftTuple.factA, leftTuple.factB, leftTuple.factC, rightTuple.factA, outputStoreSize);
    }

    @Override
    public String toString() {
        return "JoinQuadNode";
    }

}
