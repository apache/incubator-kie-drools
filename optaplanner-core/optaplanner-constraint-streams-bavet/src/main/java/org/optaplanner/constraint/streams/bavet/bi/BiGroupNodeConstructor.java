package org.optaplanner.constraint.streams.bavet.bi;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

@FunctionalInterface
interface BiGroupNodeConstructor<A, B, Tuple_ extends Tuple> {

    AbstractGroupBiNode<A, B, Tuple_, ?, ?, ?> apply(int inputStoreIndex, TupleLifecycle<Tuple_> tupleLifecycle,
            int outputStoreSize);

}
