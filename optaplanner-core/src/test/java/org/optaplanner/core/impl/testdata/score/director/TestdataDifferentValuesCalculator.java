package org.optaplanner.core.impl.testdata.score.director;

import java.util.HashSet;
import java.util.Set;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataDifferentValuesCalculator implements EasyScoreCalculator<TestdataSolution> {

    @Override
    public SimpleScore calculateScore(TestdataSolution solution) {
        int score = 0;

        Set<TestdataValue> alreadyUsedValues = new HashSet<>();

        for (TestdataEntity entity : solution.getEntityList()) {
            if (entity.getValue() != null) {
                TestdataValue value = entity.getValue();
                if (alreadyUsedValues.contains(value)) {
                    score -= 1;
                } else {
                    alreadyUsedValues.add(value);
                }
            }
        }
        return SimpleScore.of(score);
    }
}
