package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

@FunctionalInterface
interface QuadGroupNodeConstructor<A, B, C, D, Tuple_ extends Tuple> {

    AbstractGroupQuadNode<A, B, C, D, Tuple_, ?, ?, ?, ?> apply(int inputStoreIndex,
            TupleLifecycle<Tuple_> nextNodesTupleLifecycle, int outputStoreSize);

}
