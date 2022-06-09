package org.optaplanner.core.api.score.stream;

import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;

final class DefaultTriConstraintCollector<A, B, C, ResultContainer_, Result_>
        implements TriConstraintCollector<A, B, C, ResultContainer_, Result_> {

    private final Supplier<ResultContainer_> supplier;
    private final QuadFunction<ResultContainer_, A, B, C, Runnable> accumulator;
    private final Function<ResultContainer_, Result_> finisher;

    public DefaultTriConstraintCollector(Supplier<ResultContainer_> supplier,
            QuadFunction<ResultContainer_, A, B, C, Runnable> accumulator,
            Function<ResultContainer_, Result_> finisher) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.finisher = finisher;
    }

    @Override
    public Supplier<ResultContainer_> supplier() {
        return supplier;
    }

    @Override
    public QuadFunction<ResultContainer_, A, B, C, Runnable> accumulator() {
        return accumulator;
    }

    @Override
    public Function<ResultContainer_, Result_> finisher() {
        return finisher;
    }

}
