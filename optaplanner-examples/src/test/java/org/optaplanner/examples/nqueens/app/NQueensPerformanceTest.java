package org.optaplanner.examples.nqueens.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.nqueens.domain.NQueens;

class NQueensPerformanceTest extends SolverPerformanceTest<NQueens, SimpleScore> {

    @Override
    protected NQueensApp createCommonApp() {
        return new NQueensApp();
    }

    @Override
    protected Stream<TestData<SimpleScore>> testData() {
        return Stream.of(
                testData("data/nqueens/unsolved/16queens.xml", SimpleScore.ZERO, EnvironmentMode.REPRODUCIBLE),
                testData("data/nqueens/unsolved/8queens.xml", SimpleScore.ZERO, EnvironmentMode.FAST_ASSERT),
                testData("data/nqueens/unsolved/4queens.xml", SimpleScore.ZERO, EnvironmentMode.FULL_ASSERT));
    }
}
