package org.optaplanner.core.impl.testdata.domain.constraintconfiguration;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

public final class TestdataConstraintWeightEasyScoreCalculator
        implements EasyScoreCalculator<TestdataConstraintConfigurationSolution, SimpleScore> {

    @Override
    public SimpleScore calculateScore(TestdataConstraintConfigurationSolution solution) {
        SimpleScore constraintWeight = solution.getConstraintConfiguration().getFirstWeight();
        return constraintWeight.multiply(solution.getEntityList().size());
    }
}
