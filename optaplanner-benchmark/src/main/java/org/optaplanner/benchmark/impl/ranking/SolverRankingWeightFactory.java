package org.optaplanner.benchmark.impl.ranking;

import java.util.List;

import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;

/**
 * Defines an interface for classes that will be used to rank solver benchmarks
 * in order of their respective performance.
 */
public interface SolverRankingWeightFactory {

    /**
     * The ranking function. Takes the provided solverBenchmarkResultList and ranks them.
     *
     * @param solverBenchmarkResultList never null
     * @param solverBenchmarkResult never null
     * @return never null
     */
    Comparable createRankingWeight(List<SolverBenchmarkResult> solverBenchmarkResultList,
            SolverBenchmarkResult solverBenchmarkResult);

}
