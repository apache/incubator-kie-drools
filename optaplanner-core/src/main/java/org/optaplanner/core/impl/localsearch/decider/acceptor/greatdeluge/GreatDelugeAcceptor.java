package org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class GreatDelugeAcceptor extends AbstractAcceptor {

    private Score initialWaterLevel;
    private Score waterLevelIncrementScore;
    private Double waterLevelIncrementRatio;

    private Score startingWaterLevel = null;

    private Score currentWaterLevel = null;
    private Double currentWaterLevelRatio = null;

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
        startingWaterLevel = initialWaterLevel != null ? initialWaterLevel : phaseScope.getBestScore();
        if (waterLevelIncrementRatio != null) {
            currentWaterLevelRatio = 0.0;
        }
        currentWaterLevel = startingWaterLevel;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        startingWaterLevel = null;
        if (waterLevelIncrementRatio != null) {
            currentWaterLevelRatio = null;
        }
        currentWaterLevel = null;
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(currentWaterLevel) >= 0) {
            return true;
        }
        Score lastStepScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
        if (moveScore.compareTo(lastStepScore) > 0) {
            // Aspiration
            return true;
        }
        return false;
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        if (waterLevelIncrementScore != null) {
            currentWaterLevel = currentWaterLevel.add(waterLevelIncrementScore);
        } else {
            // Avoid numerical instability: SimpleScore.of(500).multiply(0.000_001) underflows to zero
            currentWaterLevelRatio += waterLevelIncrementRatio;
            currentWaterLevel = startingWaterLevel.add(
                    // TODO targetWaterLevel.subtract(startingWaterLevel).multiply(waterLevelIncrementRatio);
                    // The startingWaterLevel.negate() is short for zeroScore.subtract(startingWaterLevel)
                    startingWaterLevel.negate().multiply(currentWaterLevelRatio));
        }
    }

}
