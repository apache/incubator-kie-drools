package org.optaplanner.examples.coachshuttlegathering.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.common.app.SolverPerformanceTest;

class CoachShuttleGatheringPerformanceTest
        extends SolverPerformanceTest<CoachShuttleGatheringSolution, HardSoftLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/coachshuttlegathering/unsolved/demo01.xml";

    @Override
    protected CoachShuttleGatheringApp createCommonApp() {
        return new CoachShuttleGatheringApp();
    }

    @Override
    protected Stream<TestData<HardSoftLongScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardSoftLongScore.of(0, -389030), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardSoftLongScore.of(0, -389030), EnvironmentMode.FAST_ASSERT));
    }
}
