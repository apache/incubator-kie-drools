package org.optaplanner.examples.flightcrewscheduling.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;

class FlightCrewSchedulingPerformanceTest extends SolverPerformanceTest<FlightCrewSolution, HardSoftLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/flightcrewscheduling/unsolved/175flights-7days-Europe.xlsx";

    @Override
    protected FlightCrewSchedulingApp createCommonApp() {
        return new FlightCrewSchedulingApp();
    }

    @Override
    protected Stream<TestData<HardSoftLongScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardSoftLongScore.ofSoft(-129000000), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardSoftLongScore.ofSoft(-129000000), EnvironmentMode.FAST_ASSERT));
    }
}
