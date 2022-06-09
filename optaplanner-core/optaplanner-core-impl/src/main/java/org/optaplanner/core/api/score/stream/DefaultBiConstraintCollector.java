package org.optaplanner.core.api.score.stream;

import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

final class DefaultBiConstraintCollector<A, B, ResultContainer_, Result_>
        implements BiConstraintCollector<A, B, ResultContainer_, Result_> {

    private final Supplier<ResultContainer_> supplier;
    private final TriFunction<ResultContainer_, A, B, Runnable> accumulator;
    private final Function<ResultContainer_, Result_> finisher;

    public DefaultBiConstraintCollector(Supplier<ResultContainer_> supplier,
            TriFunction<ResultContainer_, A, B, Runnable> accumulator,
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
    public TriFunction<ResultContainer_, A, B, Runnable> accumulator() {
        return accumulator;
    }

    @Override
    public Function<ResultContainer_, Result_> finisher() {
        return finisher;
    }

}
