package org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.ScoreUtils;

public class GreatDelugeAcceptor extends AbstractAcceptor {

    // Good value to come out from. Source: https://github.com/UniTime/cpsolver from Tomas Muller
    private static final double DEFAULT_WATER_LEVEL_INCREMENT_RATIO = 0.00_000_005;

    private Score initialWaterLevel = null;

    private Score waterLevelIncrementScore = null;
    private Double waterLevelIncrementRatio = DEFAULT_WATER_LEVEL_INCREMENT_RATIO;

    private Score currentWaterLevel = null;

    public Score getWaterLevelIncrementScore() {
        return this.waterLevelIncrementScore;
    }

    public void setWaterLevelIncrementScore(Score waterLevelIncrementScore) {
        this.waterLevelIncrementScore = waterLevelIncrementScore;
    }

    public Score getInitialWaterLevel() {
        return this.initialWaterLevel;
    }

    public void setInitialWaterLevel(Score initialLevel) {
        this.initialWaterLevel = initialLevel;
    }

    public Double getWaterLevelIncrementRatio() {
        return this.waterLevelIncrementRatio;
    }

    public void setWaterLevelIncrementRatio(Double waterLevelIncrementRatio) {
        this.waterLevelIncrementRatio = waterLevelIncrementRatio;
    }

    @Override
    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        if (initialWaterLevel != null) {
            for (double initialLevelLevel : ScoreUtils.extractLevelDoubles(initialWaterLevel)) {
                if (initialLevelLevel < 0.0) {
                    throw new IllegalArgumentException("The initial level (" + initialWaterLevel
                                                               + ") cannot have negative level (" + initialLevelLevel + ").");
                }
            }
            currentWaterLevel = initialWaterLevel;
        } else {
            currentWaterLevel = phaseScope.getBestScore().negate();
        }
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        initialWaterLevel = null;
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        Score moveScore = moveScope.getScore();
        return moveScore.compareTo(currentWaterLevel.negate()) >= 0;
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        if (waterLevelIncrementScore != null) {
            currentWaterLevel = currentWaterLevel.subtract(waterLevelIncrementScore);
        } else {
            Score increment = currentWaterLevel.multiply(waterLevelIncrementRatio);
            currentWaterLevel = currentWaterLevel.subtract(increment);
        }
    }

}
