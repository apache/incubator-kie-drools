package org.optaplanner.examples.travelingtournament.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

class TravelingTournamentPerformanceTest extends SolverPerformanceTest<TravelingTournament, HardSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/travelingtournament/unsolved/1-nl10.xml";

    @Override
    protected TravelingTournamentApp createCommonApp() {
        return new TravelingTournamentApp();
    }

    @Override
    protected Stream<TestData<HardSoftScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardSoftScore.ofSoft(-73073), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardSoftScore.ofSoft(-73073), EnvironmentMode.FAST_ASSERT));
    }
}
