package org.optaplanner.constraint.streams.bavet.common;

import java.util.Objects;

public abstract class AbstractConditionalTupleLifecycle<Tuple_ extends Tuple>
        implements TupleLifecycle<Tuple_> {

    private final TupleLifecycle<Tuple_> tupleLifecycle;

    protected AbstractConditionalTupleLifecycle(TupleLifecycle<Tuple_> tupleLifecycle) {
        this.tupleLifecycle = Objects.requireNonNull(tupleLifecycle);
    }

    @Override
    public final void insert(Tuple_ tuple) {
        if (test(tuple)) {
            tupleLifecycle.insert(tuple);
        }
    }

    @Override
    public final void update(Tuple_ tuple) {
        if (test(tuple)) {
            tupleLifecycle.update(tuple);
        } else {
            tupleLifecycle.retract(tuple);
        }
    }

    @Override
    public final void retract(Tuple_ tuple) {
        tupleLifecycle.retract(tuple);
    }

    abstract protected boolean test(Tuple_ tuple);

}
