package org.optaplanner.core.impl.score;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

public class DummySimpleScoreEasyScoreCalculator<Solution_> implements EasyScoreCalculator<Solution_, SimpleScore> {

    @Override
    public SimpleScore calculateScore(Solution_ solution_) {
        return SimpleScore.of(0);
    }

}
