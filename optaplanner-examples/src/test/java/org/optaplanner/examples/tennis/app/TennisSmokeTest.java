package org.optaplanner.examples.tennis.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.tennis.domain.TennisSolution;

class TennisSmokeTest extends SolverSmokeTest<TennisSolution, HardMediumSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/tennis/unsolved/munich-7teams.json";

    @Override
    protected TennisApp createCommonApp() {
        return new TennisApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        HardMediumSoftScore.of(0, -27239, -23706),
                        HardMediumSoftScore.of(0, -27239, -23706)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        HardMediumSoftScore.of(0, -27239, -23706),
                        HardMediumSoftScore.of(0, -27239, -23706)));
    }
}
