package org.optaplanner.constraint.streams.bavet.common;

import static org.optaplanner.constraint.streams.bavet.common.BavetTupleState.DEAD;

public final class ExistsCounter<Tuple_ extends Tuple> {

    final Tuple_ leftTuple;
    BavetTupleState state = DEAD;
    int countRight = 0;

    ExistsCounter(Tuple_ leftTuple) {
        this.leftTuple = leftTuple;
    }

    @Override
    public String toString() {
        return "Counter(" + leftTuple + ")";
    }

}
