package org.optaplanner.examples.app;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class GeneralOptaPlannerBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new GeneralOptaPlannerBenchmarkApp().buildAndBenchmark(args);
    }

    public GeneralOptaPlannerBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/app/benchmark/generalOptaPlannerBenchmarkConfig.xml"),
                new ArgOption("template",
                        "org/optaplanner/examples/app/benchmark/generalOptaPlannerBenchmarkConfigTemplate.xml.ftl", true));
    }

}
