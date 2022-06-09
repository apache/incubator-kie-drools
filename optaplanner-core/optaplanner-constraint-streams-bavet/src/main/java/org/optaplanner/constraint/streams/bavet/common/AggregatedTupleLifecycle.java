package org.optaplanner.constraint.streams.bavet.common;

final class AggregatedTupleLifecycle<Tuple_ extends Tuple> implements TupleLifecycle<Tuple_> {
    private final TupleLifecycle<Tuple_>[] lifecycles;

    public AggregatedTupleLifecycle(TupleLifecycle<Tuple_>[] lifecycles) {
        this.lifecycles = lifecycles;
    }

    @Override
    public void insert(Tuple_ tuple) {
        for (TupleLifecycle<Tuple_> lifecycle : lifecycles) {
            lifecycle.insert(tuple);
        }
    }

    @Override
    public void update(Tuple_ tuple) {
        for (TupleLifecycle<Tuple_> lifecycle : lifecycles) {
            lifecycle.update(tuple);
        }
    }

    @Override
    public void retract(Tuple_ tuple) {
        for (TupleLifecycle<Tuple_> lifecycle : lifecycles) {
            lifecycle.retract(tuple);
        }
    }
}
