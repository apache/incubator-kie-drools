package org.optaplanner.examples.vehiclerouting.optional.benchmark;

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class VehicleRoutingBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new VehicleRoutingBenchmarkApp().buildAndBenchmark(args);
    }

    public VehicleRoutingBenchmarkApp() {
        super(
                new ArgOption("default",
                        "org/optaplanner/examples/vehiclerouting/optional/benchmark/vehicleRoutingBenchmarkConfig.xml"),
                new ArgOption("scoreDirector",
                        "org/optaplanner/examples/vehiclerouting/optional/benchmark/vehicleRoutingScoreDirectorBenchmarkConfig.xml"),
                new ArgOption("moveSelectorListNearBy",
                        "org/optaplanner/examples/vehiclerouting/optional/benchmark/vehicleRoutingBenchmarkConfigListNearby.xml"));
    }

}
