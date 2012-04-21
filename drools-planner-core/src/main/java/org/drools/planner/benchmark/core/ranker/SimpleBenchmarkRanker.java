package org.drools.planner.benchmark.core.ranker;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.drools.planner.benchmark.api.BenchmarkRanker;
import org.drools.planner.benchmark.core.SolverBenchmark;

/**
 * This benchmark ranker simply ranks solver benchmarks by the provided comparator. 
 */
public class SimpleBenchmarkRanker implements BenchmarkRanker {

    private List<SolverBenchmark> rankedBenchmarks;

    /**
     * Rank the benchmarks based on the provided comparator.
     */
    public void rank(List<SolverBenchmark> benchmarks, Comparator<SolverBenchmark> comparator) {
        rankedBenchmarks = benchmarks;
        Collections.sort(rankedBenchmarks, comparator);
        Collections.reverse(rankedBenchmarks);
    }

    /**
     * Benchmark with the lowest raking will be the "biggest" one in the Comparator terms.
     */
    public int getRanking(SolverBenchmark benchmark) {
        return rankedBenchmarks.indexOf(benchmark);
    }

}
