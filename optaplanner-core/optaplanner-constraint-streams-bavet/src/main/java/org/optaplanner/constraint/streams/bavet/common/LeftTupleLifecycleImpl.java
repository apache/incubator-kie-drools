package org.optaplanner.constraint.streams.bavet.common;

import java.util.Objects;

final class LeftTupleLifecycleImpl<Tuple_ extends Tuple>
        implements TupleLifecycle<Tuple_> {

    private final LeftTupleLifecycle<Tuple_> leftTupleLifecycle;

    LeftTupleLifecycleImpl(LeftTupleLifecycle<Tuple_> leftTupleLifecycle) {
        this.leftTupleLifecycle = Objects.requireNonNull(leftTupleLifecycle);
    }

    @Override
    public void insert(Tuple_ tuple) {
        leftTupleLifecycle.insertLeft(tuple);
    }

    @Override
    public void update(Tuple_ tuple) {
        leftTupleLifecycle.updateLeft(tuple);
    }

    @Override
    public void retract(Tuple_ tuple) {
        leftTupleLifecycle.retractLeft(tuple);
    }
}
