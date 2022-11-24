package org.optaplanner.examples.nqueens.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.nqueens.domain.NQueens;

class NQueensSmokeTest extends SolverSmokeTest<NQueens, SimpleScore> {

    @Override
    protected NQueensApp createCommonApp() {
        return new NQueensApp();
    }

    @Override
    protected Stream<TestData<SimpleScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, "data/nqueens/unsolved/16queens.json",
                        SimpleScore.ZERO,
                        SimpleScore.ZERO,
                        SimpleScore.ZERO),
                TestData.of(ConstraintStreamImplType.BAVET, "data/nqueens/unsolved/16queens.json",
                        SimpleScore.ZERO,
                        SimpleScore.ZERO,
                        SimpleScore.ZERO));
    }
}
