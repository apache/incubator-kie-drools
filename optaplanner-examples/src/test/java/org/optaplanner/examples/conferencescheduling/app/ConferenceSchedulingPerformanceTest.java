
package org.optaplanner.examples.conferencescheduling.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;

class ConferenceSchedulingPerformanceTest extends SolverPerformanceTest<ConferenceSolution, HardMediumSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/conferencescheduling/unsolved/72talks-12timeslots-10rooms.xlsx";

    @Override
    protected ConferenceSchedulingApp createCommonApp() {
        return new ConferenceSchedulingApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardMediumSoftScore.of(0, 0, -1027755), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardMediumSoftScore.of(0, 0, -1119825), EnvironmentMode.FAST_ASSERT));
    }
}
