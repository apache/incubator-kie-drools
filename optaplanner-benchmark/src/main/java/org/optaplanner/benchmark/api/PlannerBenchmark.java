package org.optaplanner.benchmark.api;

import java.io.File;

/**
 * A planner benchmark that runs a number of single benchmarks.
 * <p>
 * Build by a {@link PlannerBenchmarkFactory}.
 */
public interface PlannerBenchmark {

    /**
     * Run all the single benchmarks and create an overview report.
     *
     * @return never null, the directory in which the benchmark results are stored
     */
    File benchmark();

    /**
     * Run all the single benchmarks, create an overview report
     * and show it in the default browser.
     *
     * @return never null, the directory in which the benchmark results are stored
     */
    File benchmarkAndShowReportInBrowser();

}
