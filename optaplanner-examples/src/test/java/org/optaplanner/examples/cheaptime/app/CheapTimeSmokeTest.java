package org.optaplanner.examples.cheaptime.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.common.app.SolverSmokeTest;

class CheapTimeSmokeTest extends SolverSmokeTest<CheapTimeSolution, HardMediumSoftLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/cheaptime/unsolved/instance00.xml";

    @Override
    protected CheapTimeApp createCommonApp() {
        return new CheapTimeApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftLongScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        HardMediumSoftLongScore.ofUninitialized(-142, -37767, -978609121833854L, -14970),
                        HardMediumSoftLongScore.ofUninitialized(-234, -25978, -805181550079606L, -9016)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        HardMediumSoftLongScore.of(0, -1005095520893824L, -23053),
                        HardMediumSoftLongScore.ofUninitialized(-114, -3878, -1002000826403606L, -16980)));
    }
}
