package org.optaplanner.examples.nqueens.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class NQueensBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new NQueensBenchmarkApp().buildAndBenchmark(args);
    }

    public NQueensBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/nqueens/optional/benchmark/nqueensBenchmarkConfig.xml"),
                new ArgOption("stepLimit",
                        "org/optaplanner/examples/nqueens/optional/benchmark/nqueensStepLimitBenchmarkConfig.xml"),
                new ArgOption("scoreDirector",
                        "org/optaplanner/examples/nqueens/optional/benchmark/nqueensScoreDirectorBenchmarkConfig.xml"));
    }

}
