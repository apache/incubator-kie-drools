package org.optaplanner.examples.tsp.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.tsp.domain.TspSolution;

class TspPerformanceTest extends SolverPerformanceTest<TspSolution, SimpleLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/tsp/unsolved/europe40.xml";

    @Override
    protected TspApp createCommonApp() {
        return new TspApp();
    }

    @Override
    protected Stream<TestData<SimpleLongScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, SimpleLongScore.of(-216469618), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, SimpleLongScore.of(-217458433), EnvironmentMode.FAST_ASSERT));
    }
}
