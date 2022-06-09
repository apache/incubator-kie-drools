package org.optaplanner.examples.cloudbalancing.app;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingEasyScoreCalculator;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;

class CloudBalancingSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<CloudBalance> {

    @Override
    protected CommonApp<CloudBalance> createCommonApp() {
        return new CloudBalancingApp();
    }

    @Override
    protected Class<CloudBalancingEasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return CloudBalancingEasyScoreCalculator.class;
    }
}
