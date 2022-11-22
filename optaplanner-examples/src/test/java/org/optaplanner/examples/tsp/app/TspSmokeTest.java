package org.optaplanner.examples.tsp.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.tsp.domain.TspSolution;

class TspSmokeTest extends SolverSmokeTest<TspSolution, SimpleLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/tsp/unsolved/europe40.json";

    @Override
    protected TspApp createCommonApp() {
        return new TspApp();
    }

    @Override
    protected Stream<TestData<SimpleLongScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        SimpleLongScore.of(-217364246),
                        SimpleLongScore.of(-217364246)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        SimpleLongScore.of(-216469618),
                        SimpleLongScore.of(-217364246)));
    }
}
