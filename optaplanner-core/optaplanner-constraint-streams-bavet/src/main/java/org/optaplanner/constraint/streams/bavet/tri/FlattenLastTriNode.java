package org.optaplanner.constraint.streams.bavet.tri;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractFlattenLastNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

final class FlattenLastTriNode<A, B, C, NewC>
        extends AbstractFlattenLastNode<TriTuple<A, B, C>, TriTuple<A, B, NewC>, C, NewC> {

    private final int outputStoreSize;

    FlattenLastTriNode(int flattenLastStoreIndex, Function<C, Iterable<NewC>> mappingFunction,
            TupleLifecycle<TriTuple<A, B, NewC>> nextNodesTupleLifecycle, int outputStoreSize) {
        super(flattenLastStoreIndex, mappingFunction, nextNodesTupleLifecycle);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected TriTuple<A, B, NewC> createTuple(TriTuple<A, B, C> originalTuple, NewC newC) {
        return new TriTuple<>(originalTuple.factA, originalTuple.factB, newC, outputStoreSize);
    }

    @Override
    protected C getEffectiveFactIn(TriTuple<A, B, C> tuple) {
        return tuple.factC;
    }

    @Override
    protected NewC getEffectiveFactOut(TriTuple<A, B, NewC> outTuple) {
        return outTuple.factC;
    }
}
