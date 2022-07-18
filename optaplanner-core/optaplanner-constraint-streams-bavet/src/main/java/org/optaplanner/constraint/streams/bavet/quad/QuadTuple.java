
package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

public interface QuadTuple<A, B, C, D> extends Tuple {

    A getFactA();

    B getFactB();

    C getFactC();

    D getFactD();

}
