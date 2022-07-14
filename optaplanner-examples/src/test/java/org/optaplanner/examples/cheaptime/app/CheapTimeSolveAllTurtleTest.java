package org.optaplanner.examples.cheaptime.app;

import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.optional.score.CheapTimeEasyScoreCalculator;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;

class CheapTimeSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<CheapTimeSolution> {

    @Override
    protected CommonApp<CheapTimeSolution> createCommonApp() {
        return new CheapTimeApp();
    }

    @Override
    protected Class<CheapTimeEasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return CheapTimeEasyScoreCalculator.class;
    }

}
