package org.optaplanner.examples.tsp.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.optional.score.TspEasyScoreCalculator;

class TspSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<TspSolution> {

    @Override
    protected CommonApp<TspSolution> createCommonApp() {
        return new TspApp();
    }

    @Override
    protected Class<TspEasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return TspEasyScoreCalculator.class;
    }
}
