package org.optaplanner.examples.projectjobscheduling.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class ProjectJobSchedulingBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new ProjectJobSchedulingBenchmarkApp().buildAndBenchmark(args);
    }

    public ProjectJobSchedulingBenchmarkApp() {
        super(
                new ArgOption("template",
                        "org/optaplanner/examples/projectjobscheduling/optional/benchmark/projectJobSchedulingBenchmarkConfigTemplate.xml.ftl",
                        true));
    }

}
