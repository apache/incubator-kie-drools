package org.optaplanner.examples.cheaptime.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class CheapTimeBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new CheapTimeBenchmarkApp().buildAndBenchmark(args);
    }

    public CheapTimeBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/cheaptime/optional/benchmark/cheapTimeBenchmarkConfig.xml"),
                new ArgOption("template",
                        "org/optaplanner/examples/cheaptime/optional/benchmark/cheapTimeBenchmarkConfigTemplate.xml.ftl",
                        true));
    }

}
