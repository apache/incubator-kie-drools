package org.optaplanner.examples.pas.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class PatientAdmissionScheduleBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new PatientAdmissionScheduleBenchmarkApp().buildAndBenchmark(args);
    }

    public PatientAdmissionScheduleBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/pas/optional/benchmark/patientAdmissionScheduleBenchmarkConfig.xml"));
    }

}
