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
    private Double rainSpeedRatio = null;

    private int levelsLength = -1;
    private double[] initialLevelScoreLevels;
    private double[] initialLevelScoreLevelsNegative;
    private double[] levelScoreLevels;

    private double levelMinimum = 0;
    private final double THRESHOLD = .0000001;

    // Good value to come out from
    private final double DEFAULTRAINSPEEDRATIO = 0.99999995;

    public void setInitialLevels(Score initialLevel) { this.initialLevel = initialLevel; }

    public void setRainSpeed(Double rainSpeed) { this.rainSpeed = rainSpeed; }

    public void setRainSpeedRatio(Double rainSpeedRatio) {this.rainSpeedRatio = rainSpeedRatio; }

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
            initialLevelScoreLevelsNegative = ScoreUtils.extractLevelDoubles(phaseScope.getBestScore());
            logger.info(phaseScope.getBestScore().toShortString());
            levelScoreLevels = initialLevelScoreLevelsNegative;

            for (int i = 0; i < levelScoreLevels.length; i++) {

                if (Math.abs(levelScoreLevels[i]) < THRESHOLD) {
                    logger.info(Double.toString(levelScoreLevels[i]));
                    continue;
                }
                levelScoreLevels[i] = -levelScoreLevels[i]+5;
                logger.info(Double.toString(levelScoreLevels[i]));
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

            if (moveScoreLevel > -levelScoreLevel) {
                return true;
            } else if (Math.abs(moveScoreLevel + levelScoreLevel) < THRESHOLD) {

                if (i == levelsLength -1) {
                    return true;
                }
                continue;
            } else {
                return false;
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
            } else {
                levelScoreLevels[i] = levelScoreLevels[i] * DEFAULTRAINSPEEDRATIO;
            }
            if (levelScoreLevels[i] < levelMinimum) {
                levelScoreLevels[i] = levelMinimum;
            }
        }
    }
}
