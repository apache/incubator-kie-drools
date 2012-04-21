package org.drools.planner.benchmark.api;

import java.util.Comparator;
import java.util.List;

import org.drools.planner.benchmark.core.SolverBenchmark;

/**
 * Defines an interface for classes that will be used to rank solver benchmarks in order of performance.
 * 
 */
public interface BenchmarkRanker {

    /**
     * The ranking function. Takes the provided benchmarks and ranks them.
     * @param benchmarks Benchmarks to rank.
     * @param comparator Comparator by which to compare the benchmarks. The ranker may choose to disregard it and use its own Comparator.
     */
    public void rank(List<SolverBenchmark> benchmarks, Comparator<SolverBenchmark> comparator);

    /**
     * Get the rank of the solver benchmark. Call only after @see{rank()} had been called.
     * @param benchmark The benchmark in question.
     * @return Rank of the benchmark. The lower the rank, the better the benchmark.
     */
    public int getRanking(SolverBenchmark benchmark);

}
