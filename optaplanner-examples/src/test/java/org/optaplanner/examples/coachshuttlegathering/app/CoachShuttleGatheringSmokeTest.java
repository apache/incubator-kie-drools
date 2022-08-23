package org.optaplanner.examples.coachshuttlegathering.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.common.app.SolverSmokeTest;

class CoachShuttleGatheringSmokeTest
        extends SolverSmokeTest<CoachShuttleGatheringSolution, HardSoftLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/coachshuttlegathering/unsolved/demo01.xml";

    @Override
    protected CoachShuttleGatheringApp createCommonApp() {
        return new CoachShuttleGatheringApp();
    }

    @Override
    protected Stream<TestData<HardSoftLongScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        HardSoftLongScore.ofSoft(-384040),
                        HardSoftLongScore.ofSoft(-384040)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        HardSoftLongScore.ofSoft(-384040),
                        HardSoftLongScore.ofSoft(-384040)));
    }
}
