package org.optaplanner.examples.vehiclerouting.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.ImportDirSolveAllTurtleTest;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingEasyScoreCalculator;

class VehicleRoutingSolveAllTurtleTest extends ImportDirSolveAllTurtleTest<VehicleRoutingSolution> {

    @Override
    protected CommonApp<VehicleRoutingSolution> createCommonApp() {
        return new VehicleRoutingApp();
    }

    @Override
    protected Class<VehicleRoutingEasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return VehicleRoutingEasyScoreCalculator.class;
    }
}
