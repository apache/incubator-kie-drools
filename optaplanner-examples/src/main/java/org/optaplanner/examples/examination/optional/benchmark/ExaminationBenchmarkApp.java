package org.optaplanner.examples.examination.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class ExaminationBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new ExaminationBenchmarkApp().buildAndBenchmark(args);
    }

    public ExaminationBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/examination/optional/benchmark/examinationBenchmarkConfig.xml"),
                new ArgOption("stepLimit",
                        "org/optaplanner/examples/examination/optional/benchmark/examinationStepLimitBenchmarkConfig.xml"));
    }

}
