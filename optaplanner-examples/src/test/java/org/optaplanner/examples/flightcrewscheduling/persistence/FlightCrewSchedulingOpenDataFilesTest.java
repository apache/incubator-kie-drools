package org.optaplanner.examples.flightcrewscheduling.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.flightcrewscheduling.app.FlightCrewSchedulingApp;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;

class FlightCrewSchedulingOpenDataFilesTest extends OpenDataFilesTest<FlightCrewSolution> {

    @Override
    protected CommonApp<FlightCrewSolution> createCommonApp() {
        return new FlightCrewSchedulingApp();
    }
}
