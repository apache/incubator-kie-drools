package org.optaplanner.examples.nurserostering.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class NurseRosteringBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new NurseRosteringBenchmarkApp().buildAndBenchmark(args);
    }

    public NurseRosteringBenchmarkApp() {
        super(
                new ArgOption("sprint",
                        "org/optaplanner/examples/nurserostering/optional/benchmark/nurseRosteringSprintBenchmarkConfig.xml"),
                new ArgOption("medium",
                        "org/optaplanner/examples/nurserostering/optional/benchmark/nurseRosteringMediumBenchmarkConfig.xml"),
                new ArgOption("long",
                        "org/optaplanner/examples/nurserostering/optional/benchmark/nurseRosteringLongBenchmarkConfig.xml"),
                new ArgOption("stepLimit",
                        "org/optaplanner/examples/nurserostering/optional/benchmark/nurseRosteringStepLimitBenchmarkConfig.xml"));
    }

}
