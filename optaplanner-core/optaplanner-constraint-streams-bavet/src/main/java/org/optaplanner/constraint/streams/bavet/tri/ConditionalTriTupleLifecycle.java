package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.AbstractConditionalTupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.TriPredicate;

final class ConditionalTriTupleLifecycle<A, B, C> extends AbstractConditionalTupleLifecycle<TriTuple<A, B, C>> {
    private final TriPredicate<A, B, C> predicate;

    public ConditionalTriTupleLifecycle(TriPredicate<A, B, C> predicate, TupleLifecycle<TriTuple<A, B, C>> tupleLifecycle) {
        super(tupleLifecycle);
        this.predicate = predicate;
    }

    @Override
    protected boolean test(TriTuple<A, B, C> tuple) {
        return predicate.test(tuple.getFactA(), tuple.getFactB(), tuple.getFactC());
    }

}
