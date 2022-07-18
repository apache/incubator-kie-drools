package org.optaplanner.constraint.streams.bavet.bi;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

public interface BiTuple<A, B> extends Tuple {

    A getFactA();

    B getFactB();

}
