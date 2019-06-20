package org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.ScoreUtils;

public class GreatDelugeAcceptor extends AbstractAcceptor {

    private Score initialLevel = null;

    private Double rainSpeed = null;
    private Double rainSpeedRatio = DEFAULT_RAIN_SPEED_RATIO;

    private int levelsLength = -1;
    private double[] initialLevelScoreLevels;
    private double[] levelScoreLevels;

    private double levelMinimum = 0;
    private static final double THRESHOLD = .0000001;

    // Good value to come out from. Source: https://github.com/UniTime/cpsolver from Tomas Muller
    private static final double DEFAULT_RAIN_SPEED_RATIO = 0.99_999_995;

    public void setInitialLevels(Score initialLevel) { this.initialLevel = initialLevel; }

    public Score getInitialLevels() { return this.initialLevel; }

    public void setRainSpeed(Double rainSpeed) {
        this.rainSpeed = rainSpeed;
        this.rainSpeedRatio = null;
    }

    public Double getRainSpeed() { return this.rainSpeed; }

    public void setRainSpeedRatio(Double rainSpeedRatio) {this.rainSpeedRatio = rainSpeedRatio; }

    public Double getRainSpeedRatio() { return this.rainSpeedRatio; }

    public double[] getLevelScoreLevels() { return this.levelScoreLevels; }

    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);

        if (initialLevel != null) {
            for (double initialLevelLevel : ScoreUtils.extractLevelDoubles(initialLevel)) {
                if (initialLevelLevel < 0.0) {
                    throw new IllegalArgumentException("The initial level (" + initialLevel
                            + ") cannot have negative level (" + initialLevelLevel + ").");
                }
            }
            initialLevelScoreLevels = ScoreUtils.extractLevelDoubles(initialLevel);
            levelScoreLevels = initialLevelScoreLevels;
        } else {
            double[] initialLevelScoreLevelsNegative;
            initialLevelScoreLevelsNegative = ScoreUtils.extractLevelDoubles(phaseScope.getBestScore());
            levelScoreLevels = initialLevelScoreLevelsNegative;

            for (int i = 0; i < levelScoreLevels.length; i++) {
                if (Math.abs(levelScoreLevels[i]) < THRESHOLD) {
                    continue;
                }
                levelScoreLevels[i] = -levelScoreLevels[i];
            }
        }
        levelsLength = levelScoreLevels.length;
    }

    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        initialLevelScoreLevels = null;
        levelScoreLevels = null;
        levelsLength = -1;
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {

        Score moveScore = moveScope.getScore();
        double[] moveScoreLevels = ScoreUtils.extractLevelDoubles(moveScore);

        for (int i = 0; i < levelsLength; i++) {
            double moveScoreLevel = moveScoreLevels[i];
            double levelScoreLevel = levelScoreLevels[i];

            double isZeroIfEqual = Math.abs(moveScoreLevel + levelScoreLevel);

            if (moveScoreLevel > -levelScoreLevel) {
                // Checks if given score is better than water level, if so, move is accepted.
                return true;
            } else if (!(isZeroIfEqual < THRESHOLD)) {
                // Returns false when above condition is not met and, new score and water level are not equal.
                return false;
            } else if (i == levelsLength - 1){
                // Returns true when new score and water level are equal and it is last iteration of cycle.
                return true;
            }
        }
        return true;
    }

    // change water level at the beginning of the step
    public void stepStarted(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        for (int i = 0; i < levelsLength; i++) {
            if (rainSpeed != null) {
                levelScoreLevels[i] = levelScoreLevels[i] - rainSpeed;
            } else if (rainSpeedRatio != null) {
                levelScoreLevels[i] = levelScoreLevels[i] * rainSpeedRatio;
            }
            if (levelScoreLevels[i] < levelMinimum) {
                levelScoreLevels[i] = levelMinimum;
            }
        }
    }
}
