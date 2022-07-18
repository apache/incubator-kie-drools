package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

public interface TriTuple<A, B, C> extends Tuple {

    A getFactA();

    B getFactB();

    C getFactC();

}
