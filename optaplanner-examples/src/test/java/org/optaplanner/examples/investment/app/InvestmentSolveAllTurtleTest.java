package org.optaplanner.examples.investment.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.optional.score.InvestmentEasyScoreCalculator;

class InvestmentSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<InvestmentSolution> {

    @Override
    protected CommonApp<InvestmentSolution> createCommonApp() {
        return new InvestmentApp();
    }

    @Override
    protected Class<InvestmentEasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return InvestmentEasyScoreCalculator.class;
    }
}
