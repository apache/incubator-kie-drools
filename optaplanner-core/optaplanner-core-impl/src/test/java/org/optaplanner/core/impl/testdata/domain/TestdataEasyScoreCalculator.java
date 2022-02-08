package org.optaplanner.core.impl.testdata.domain;

import java.util.Objects;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

public class TestdataEasyScoreCalculator implements EasyScoreCalculator<TestdataSolution, SimpleScore> {
    @Override
    public SimpleScore calculateScore(TestdataSolution testdataSolution) {
        int score = 0;
        for (TestdataEntity left : testdataSolution.getEntityList()) {
            TestdataValue value = left.getValue();
            if (value == null) {
                continue;
            }
            for (TestdataEntity right : testdataSolution.getEntityList()) {
                if (Objects.equals(right.getValue(), value)) {
                    score -= 1;
                }
            }
        }
        return SimpleScore.of(score);
    }
}
