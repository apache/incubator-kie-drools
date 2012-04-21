package org.drools.planner.benchmark.api;

import java.util.Comparator;
import java.util.List;

import org.drools.planner.benchmark.core.SolverBenchmark;

public interface BenchmarkRanker {

    public void rank(List<SolverBenchmark> benchmarks, Comparator<SolverBenchmark> comparator);

    public int getRanking(SolverBenchmark benchmark);

}
