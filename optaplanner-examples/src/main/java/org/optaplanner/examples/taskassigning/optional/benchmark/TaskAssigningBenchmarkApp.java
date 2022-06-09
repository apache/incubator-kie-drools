package org.optaplanner.examples.taskassigning.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class TaskAssigningBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new TaskAssigningBenchmarkApp().buildAndBenchmark(args);
    }

    public TaskAssigningBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/taskassigning/optional/benchmark/taskAssigningBenchmarkConfig.xml"),
                new ArgOption("scoreDirector",
                        "org/optaplanner/examples/taskassigning/optional/benchmark/taskAssigningScoreDirectorBenchmarkConfig.xml"));
    }

}
