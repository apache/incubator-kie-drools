package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

@FunctionalInterface
interface TriGroupNodeConstructor<A, B, C, Tuple_ extends Tuple> {

    AbstractGroupTriNode<A, B, C, Tuple_, ?, ?, ?, ?> apply(int inputStoreIndex, TupleLifecycle<Tuple_> tupleLifecycle,
            int outputStoreSize);

}
