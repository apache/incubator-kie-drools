package org.optaplanner.constraint.streams.bavet.uni;

import java.util.Objects;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractMapNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

final class MapUniNode<A, NewA> extends AbstractMapNode<UniTuple<A>, NewA> {

    private final Function<A, NewA> mappingFunction;

    MapUniNode(int mapStoreIndex, Function<A, NewA> mappingFunction, TupleLifecycle<UniTuple<NewA>> nextNodesTupleLifecycle,
            int outputStoreSize) {
        super(mapStoreIndex, nextNodesTupleLifecycle, outputStoreSize);
        this.mappingFunction = Objects.requireNonNull(mappingFunction);
    }

    @Override
    protected NewA map(UniTuple<A> tuple) {
        return mappingFunction.apply(tuple.getFactA());
    }

}
