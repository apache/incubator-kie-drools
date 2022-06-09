package org.optaplanner.examples.tsp.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class TspBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new TspBenchmarkApp().buildAndBenchmark(args);
    }

    public TspBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/tsp/optional/benchmark/tspBenchmarkConfig.xml"));
    }

}
