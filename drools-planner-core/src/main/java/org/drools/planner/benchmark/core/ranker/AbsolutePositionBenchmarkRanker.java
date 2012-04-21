package org.drools.planner.benchmark.core.ranker;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.drools.planner.benchmark.api.BenchmarkRanker;
import org.drools.planner.benchmark.core.SolverBenchmark;

public class AbsolutePositionBenchmarkRanker implements BenchmarkRanker {

    private List<SolverBenchmark> rankedBenchmarks;

    public void rank(List<SolverBenchmark> benchmarks, Comparator<SolverBenchmark> comparator) {
        rankedBenchmarks = benchmarks;
        Collections.sort(rankedBenchmarks, comparator);
        Collections.reverse(rankedBenchmarks);
    }

    public int getRanking(SolverBenchmark benchmark) {
        return rankedBenchmarks.indexOf(benchmark);
    }

}
