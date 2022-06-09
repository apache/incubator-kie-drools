package org.optaplanner.benchmark.api;

import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;

/**
 * If at least one of the {@link SingleBenchmarkResult}s of a {@link PlannerBenchmark} fail,
 * the {@link PlannerBenchmark} throws this exception
 * after all {@link SingleBenchmarkResult}s are finished and the benchmark report has been written.
 */
public class PlannerBenchmarkException extends RuntimeException {

    public PlannerBenchmarkException(String message, Throwable cause) {
        super(message, cause);
    }

}
