package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Objects;
import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.bavet.common.AbstractMapNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class MapBiNode<A, B, NewA> extends AbstractMapNode<BiTuple<A, B>, NewA> {

    private final BiFunction<A, B, NewA> mappingFunction;

    MapBiNode(int mapStoreIndex, BiFunction<A, B, NewA> mappingFunction, TupleLifecycle<UniTuple<NewA>> nextNodesTupleLifecycle,
            int outputStoreSize) {
        super(mapStoreIndex, nextNodesTupleLifecycle, outputStoreSize);
        this.mappingFunction = Objects.requireNonNull(mappingFunction);
    }

    @Override
    protected NewA map(BiTuple<A, B> tuple) {
        return mappingFunction.apply(tuple.getFactA(), tuple.getFactB());
    }

}
