package org.optaplanner.examples.rocktour.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

class RockTourPerformanceTest extends SolverPerformanceTest<RockTourSolution, HardMediumSoftLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/rocktour/unsolved/47shows.xlsx";

    @Override
    protected RockTourApp createCommonApp() {
        return new RockTourApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftLongScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardMediumSoftLongScore.of(0, 72725670, -6208480), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardMediumSoftLongScore.of(0, 72725039, -5186309), EnvironmentMode.FAST_ASSERT));
    }
}
