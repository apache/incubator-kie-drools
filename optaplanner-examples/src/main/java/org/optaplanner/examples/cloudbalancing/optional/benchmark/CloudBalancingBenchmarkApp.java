package org.optaplanner.examples.cloudbalancing.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class CloudBalancingBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new CloudBalancingBenchmarkApp().buildAndBenchmark(args);
    }

    public CloudBalancingBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/cloudbalancing/optional/benchmark/cloudBalancingBenchmarkConfig.xml"),
                new ArgOption("stepLimit",
                        "org/optaplanner/examples/cloudbalancing/optional/benchmark/cloudBalancingStepLimitBenchmarkConfig.xml"),
                new ArgOption("scoreDirector",
                        "org/optaplanner/examples/cloudbalancing/optional/benchmark/cloudBalancingScoreDirectorBenchmarkConfig.xml"),
                new ArgOption("template",
                        "org/optaplanner/examples/cloudbalancing/optional/benchmark/cloudBalancingBenchmarkConfigTemplate.xml.ftl",
                        true),
                new ArgOption("partitioned",
                        "org/optaplanner/examples/cloudbalancing/optional/benchmark/cloudBalancingPartitionedSearchBenchmarkConfig.xml"));
    }
}
