package org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.ScoreUtils;

public class GreatDelugeAcceptor extends AbstractAcceptor {

    private Score initialWaterLevels = null;
    private Score currentWaterLevel = null;

    private Double rainSpeedRatio = DEFAULT_RAIN_SPEED_RATIO;
    private Score rainSpeedScore = null;

    // Good value to come out from. Source: https://github.com/UniTime/cpsolver from Tomas Muller
    private static final double DEFAULT_RAIN_SPEED_RATIO = 0.99_999_995;


    public Score getRainSpeedScore() {
        return this.rainSpeedScore;
    }

    public void setRainSpeedScore(Score rainSpeedScore) {
        this.rainSpeedScore = rainSpeedScore;
        this.rainSpeedRatio = null;
    }

    public Score getInitialWaterLevels() {
        return this.initialWaterLevels;
    }

    public void setInitialWaterLevels(Score initialLevel) {
        this.initialWaterLevels = initialLevel;
    }

    public Double getRainSpeedRatio() {
        return this.rainSpeedRatio;
    }

    public void setRainSpeedRatio(Double rainSpeedRatio) {
        this.rainSpeedRatio = rainSpeedRatio;
    }

    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);

        if (initialWaterLevels != null) {
            for (double initialLevelLevel : ScoreUtils.extractLevelDoubles(initialWaterLevels)) {
                if (initialLevelLevel < 0.0) {
                    throw new IllegalArgumentException("The initial level (" + initialWaterLevels
                                                               + ") cannot have negative level (" + initialLevelLevel + ").");
                }
            }
            currentWaterLevel = initialWaterLevels;

        } else {
            currentWaterLevel = phaseScope.getBestScore().negate();
        }
    }

    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        initialWaterLevels = null;
        rainSpeedRatio = DEFAULT_RAIN_SPEED_RATIO;
        rainSpeedScore = null;
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {

        Score moveScore = moveScope.getScore();

        return moveScore.compareTo(currentWaterLevel.negate()) >= 0;
    }

    public void stepStarted(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        if (rainSpeedScore != null) {
            currentWaterLevel = currentWaterLevel.subtract(rainSpeedScore);
        } else {
            currentWaterLevel = currentWaterLevel.multiply(rainSpeedRatio);
        }
    }
}
