package org.optaplanner.core.api.score.stream;

import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;

final class DefaultQuadConstraintCollector<A, B, C, D, ResultContainer_, Result_>
        implements QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> {

    private final Supplier<ResultContainer_> supplier;
    private final PentaFunction<ResultContainer_, A, B, C, D, Runnable> accumulator;
    private final Function<ResultContainer_, Result_> finisher;

    public DefaultQuadConstraintCollector(Supplier<ResultContainer_> supplier,
            PentaFunction<ResultContainer_, A, B, C, D, Runnable> accumulator,
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
    public PentaFunction<ResultContainer_, A, B, C, D, Runnable> accumulator() {
        return accumulator;
    }

    @Override
    public Function<ResultContainer_, Result_> finisher() {
        return finisher;
    }

}
