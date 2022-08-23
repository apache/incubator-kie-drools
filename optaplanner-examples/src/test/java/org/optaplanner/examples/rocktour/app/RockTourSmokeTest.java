package org.optaplanner.examples.rocktour.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

class RockTourSmokeTest extends SolverSmokeTest<RockTourSolution, HardMediumSoftLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/rocktour/unsolved/47shows.xlsx";

    @Override
    protected RockTourApp createCommonApp() {
        return new RockTourApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftLongScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        HardMediumSoftLongScore.of(0, 72725144, -5042452),
                        HardMediumSoftLongScore.of(0, 72725144, -5042452)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        HardMediumSoftLongScore.of(0, 72726773, -4954151),
                        HardMediumSoftLongScore.of(0, 72725194, -6558488)));
    }
}
