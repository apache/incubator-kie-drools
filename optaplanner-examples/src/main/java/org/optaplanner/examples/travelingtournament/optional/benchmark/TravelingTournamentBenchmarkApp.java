package org.optaplanner.examples.travelingtournament.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class TravelingTournamentBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new TravelingTournamentBenchmarkApp().buildAndBenchmark(args);
    }

    public TravelingTournamentBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/travelingtournament/optional/benchmark/travelingTournamentBenchmarkConfig.xml"),
                new ArgOption("stepLimit",
                        "org/optaplanner/examples/travelingtournament/optional/benchmark/travelingTournamentStepLimitBenchmarkConfig.xml"));
    }

}
