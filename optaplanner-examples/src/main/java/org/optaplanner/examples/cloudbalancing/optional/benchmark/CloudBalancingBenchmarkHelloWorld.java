package org.optaplanner.examples.cloudbalancing.optional.benchmark;

import java.util.Arrays;
import java.util.List;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.aggregator.swingui.BenchmarkAggregatorFrame;
import org.optaplanner.examples.cloudbalancing.app.CloudBalancingHelloWorld;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingGenerator;

/**
 * @see CloudBalancingHelloWorld
 */
public class CloudBalancingBenchmarkHelloWorld {

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);
        boolean advanced = argList.contains("--advanced");
        if (!advanced) {
            runBasicBenchmark();
        } else {
            boolean aggregator = argList.contains("--aggregator");
            runAdvancedBenchmark(aggregator);
        }
    }

    /**
     * Basic (no benchmark XML): just benchmark the solver config
     */
    public static void runBasicBenchmark() {
        // Build the PlannerBenchmark
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                "org/optaplanner/examples/cloudbalancing/cloudBalancingSolverConfig.xml");

        CloudBalancingGenerator generator = new CloudBalancingGenerator();
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(
                generator.createCloudBalance(200, 600),
                generator.createCloudBalance(400, 1200));

        // Benchmark the problem and show it
        benchmark.benchmarkAndShowReportInBrowser();
    }

    /**
     * Advanced (benchmark XML): benchmark multiple solver configurations
     */
    public static void runAdvancedBenchmark(boolean aggregator) {
        // Build the PlannerBenchmark
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "org/optaplanner/examples/cloudbalancing/optional/benchmark/cloudBalancingBenchmarkConfig.xml");

        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
        // Benchmark the problem and show it
        benchmark.benchmarkAndShowReportInBrowser();

        // Show aggregator to aggregate multiple reports
        if (aggregator) {
            BenchmarkAggregatorFrame.createAndDisplayFromXmlResource(
                    "org/optaplanner/examples/cloudbalancing/optional/benchmark/cloudBalancingBenchmarkConfig.xml");
        }
    }

}
