package org.optaplanner.examples.flightcrewscheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;

class FlightCrewSchedulingSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<FlightCrewSolution> {

    @Override
    protected CommonApp<FlightCrewSolution> createCommonApp() {
        return new FlightCrewSchedulingApp();
    }
}
