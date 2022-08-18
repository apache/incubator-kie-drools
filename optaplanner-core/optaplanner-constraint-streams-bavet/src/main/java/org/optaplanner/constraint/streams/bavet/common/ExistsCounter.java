package org.optaplanner.constraint.streams.bavet.common;

import static org.optaplanner.constraint.streams.bavet.common.BavetTupleState.DEAD;

import java.util.function.IntPredicate;

public final class ExistsCounter<Tuple_ extends Tuple> {

    private static final IntPredicate ZERO_COUNT = count -> count == 0;
    private static final IntPredicate NON_ZERO_COUNT = count -> count > 0;

    final Tuple_ leftTuple;
    final IntPredicate alivePredicate;
    BavetTupleState state = DEAD;
    int countRight = 0;

    ExistsCounter(Tuple_ leftTuple, boolean shouldExist) {
        this.leftTuple = leftTuple;
        this.alivePredicate = shouldExist ? NON_ZERO_COUNT : ZERO_COUNT;
    }

    boolean isAlive() {
        return alivePredicate.test(countRight);
    }

    @Override
    public String toString() {
        return "Counter(" + leftTuple + ")";
    }
}
