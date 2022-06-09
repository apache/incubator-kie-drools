package org.optaplanner.examples.tennis.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.tennis.domain.TennisSolution;

class TennisPerformanceTest extends SolverPerformanceTest<TennisSolution, HardMediumSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/tennis/unsolved/munich-7teams.xml";

    @Override
    protected TennisApp createCommonApp() {
        return new TennisApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardMediumSoftScore.of(0, -27239, -23706), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardMediumSoftScore.of(0, -27239, -23706), EnvironmentMode.FAST_ASSERT));
    }
}
