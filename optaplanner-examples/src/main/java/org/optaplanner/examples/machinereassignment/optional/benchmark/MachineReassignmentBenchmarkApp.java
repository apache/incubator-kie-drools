package org.optaplanner.examples.machinereassignment.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class MachineReassignmentBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new MachineReassignmentBenchmarkApp().buildAndBenchmark(args);
    }

    public MachineReassignmentBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/machinereassignment/optional/benchmark/machineReassignmentBenchmarkConfig.xml"),
                new ArgOption("stepLimit",
                        "org/optaplanner/examples/machinereassignment/optional/benchmark/machineReassignmentStepLimitBenchmarkConfig.xml"),
                new ArgOption("scoreDirector",
                        "org/optaplanner/examples/machinereassignment/optional/benchmark/machineReassignmentScoreDirectorBenchmarkConfig.xml"),
                new ArgOption("template",
                        "org/optaplanner/examples/machinereassignment/optional/benchmark/machineReassignmentBenchmarkConfigTemplate.xml.ftl",
                        true));
    }

}
