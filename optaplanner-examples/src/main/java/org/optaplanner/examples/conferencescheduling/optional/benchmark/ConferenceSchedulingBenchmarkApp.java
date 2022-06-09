package org.optaplanner.examples.conferencescheduling.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class ConferenceSchedulingBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new ConferenceSchedulingBenchmarkApp().buildAndBenchmark(args);
    }

    public ConferenceSchedulingBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/conferencescheduling/optional/benchmark/conferenceSchedulingBenchmarkConfig.xml"));
    }

}
