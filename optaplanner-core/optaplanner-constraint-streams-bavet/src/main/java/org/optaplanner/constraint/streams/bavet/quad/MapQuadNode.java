package org.optaplanner.constraint.streams.bavet.quad;

import java.util.Objects;

import org.optaplanner.constraint.streams.bavet.common.AbstractMapNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.QuadFunction;

final class MapQuadNode<A, B, C, D, NewA> extends AbstractMapNode<QuadTuple<A, B, C, D>, NewA> {

    private final QuadFunction<A, B, C, D, NewA> mappingFunction;

    MapQuadNode(int mapStoreIndex, QuadFunction<A, B, C, D, NewA> mappingFunction,
            TupleLifecycle<UniTuple<NewA>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(mapStoreIndex, nextNodesTupleLifecycle, outputStoreSize);
        this.mappingFunction = Objects.requireNonNull(mappingFunction);
    }

    @Override
    protected NewA map(QuadTuple<A, B, C, D> tuple) {
        return mappingFunction.apply(tuple.getFactA(), tuple.getFactB(), tuple.getFactC(), tuple.getFactD());
    }

}
