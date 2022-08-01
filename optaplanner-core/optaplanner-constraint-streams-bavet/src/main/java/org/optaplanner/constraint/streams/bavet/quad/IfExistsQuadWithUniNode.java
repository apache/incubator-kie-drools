package org.optaplanner.constraint.streams.bavet.quad;

import java.util.Set;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.function.QuadFunction;

final class IfExistsQuadWithUniNode<A, B, C, D, E> extends AbstractIfExistsNode<QuadTuple<A, B, C, D>, E> {

    private final QuadFunction<A, B, C, D, IndexProperties> mappingABCD;
    private final PentaPredicate<A, B, C, D, E> filtering;

    public IfExistsQuadWithUniNode(boolean shouldExist,
            QuadFunction<A, B, C, D, IndexProperties> mappingABCD, Function<E, IndexProperties> mappingD,
            int inputStoreIndexABC, int inputStoreIndexD,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle,
            Indexer<QuadTuple<A, B, C, D>, Counter<QuadTuple<A, B, C, D>>> indexerABCD,
            Indexer<UniTuple<E>, Set<Counter<QuadTuple<A, B, C, D>>>> indexerE,
            PentaPredicate<A, B, C, D, E> filtering) {
        super(shouldExist, mappingD, inputStoreIndexABC, inputStoreIndexD, nextNodesTupleLifecycle, indexerABCD, indexerE,
                filtering != null);
        this.mappingABCD = mappingABCD;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(QuadTuple<A, B, C, D> leftTuple) {
        return mappingABCD.apply(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC(), leftTuple.getFactD());
    }

    @Override
    protected boolean testFiltering(QuadTuple<A, B, C, D> leftTuple, UniTuple<E> rightTuple) {
        return filtering.test(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC(), leftTuple.getFactD(),
                rightTuple.getFactA());
    }

    @Override
    public String toString() {
        return "IfExistsQuadWithUniNode";
    }

}
