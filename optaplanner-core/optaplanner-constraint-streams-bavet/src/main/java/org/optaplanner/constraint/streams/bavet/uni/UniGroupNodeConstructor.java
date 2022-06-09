package org.optaplanner.constraint.streams.bavet.uni;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

@FunctionalInterface
interface UniGroupNodeConstructor<A, Tuple_ extends Tuple> {

    AbstractGroupUniNode<A, Tuple_, ?, ?, ?> apply(int inputStoreIndex, TupleLifecycle<Tuple_> tupleLifecycle,
            int outputStoreSize);

}
