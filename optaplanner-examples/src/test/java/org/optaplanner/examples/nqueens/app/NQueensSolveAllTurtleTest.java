package org.optaplanner.examples.nqueens.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.optional.score.NQueensEasyScoreCalculator;

class NQueensSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<NQueens> {

    @Override
    protected CommonApp<NQueens> createCommonApp() {
        return new NQueensApp();
    }

    @Override
    protected Class<NQueensEasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return NQueensEasyScoreCalculator.class;
    }
}
