/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.localsearch.decider.acceptor.lateannealing;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.ScoreUtils;

public class LateAnnealingAcceptor extends AbstractAcceptor {

    protected int lateAnnealingSize = -1;
    protected boolean hillClimbingEnabled = true;

    protected Score[] previousScores;
    protected int lateScoreIndex = -1;

    public void setLateAnnealingSize(int lateAnnealingSize) {
        this.lateAnnealingSize = lateAnnealingSize;
    }

    public void setHillClimbingEnabled(boolean hillClimbingEnabled) {
        this.hillClimbingEnabled = hillClimbingEnabled;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        validate();
        previousScores = new Score[lateAnnealingSize];
        Score initialScore = phaseScope.getBestScore();
        for (int i = 0; i < previousScores.length; i++) {
            previousScores[i] = initialScore;
        }
        lateScoreIndex = 0;
    }

    private void validate() {
        if (lateAnnealingSize <= 0) {
            throw new IllegalArgumentException("The lateAcceptanceSize (" + lateAnnealingSize
                    + ") cannot be negative or zero.");
        }
    }

    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        Score score = moveScope.getScore();
        Score lastStepScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
        if (score.compareTo(lastStepScore) >= 0) {
            return true;
        }
        Score lateScore = previousScores[lateScoreIndex];
        Score scoreDifference = lastStepScore.subtract(score);
        double[] scoreDifferenceLevels = ScoreUtils.extractLevelDoubles(scoreDifference);
        Score lateScoreDifference = lastStepScore.subtract(lateScore);
        double[] lateScoreDifferenceLevels = ScoreUtils.extractLevelDoubles(lateScoreDifference);
        double acceptChance = 1.0;
        for (int i = 0; i < scoreDifferenceLevels.length; i++) {
            double scoreDifferenceLevel = scoreDifferenceLevels[i];
            double lateScoreDifferenceLevel = lateScoreDifferenceLevels[i];
            double acceptChanceLevel;
            if (scoreDifferenceLevel <= 0.0) {
                // In this level score is better than the lastStepScore, so do not disrupt the acceptChance
                acceptChanceLevel = 1.0;
            } else {
                acceptChanceLevel = Math.exp(-scoreDifferenceLevel / lateScoreDifferenceLevel);
            }
            acceptChance *= acceptChanceLevel;
        }
        if (moveScope.getWorkingRandom().nextDouble() < acceptChance) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        previousScores[lateScoreIndex] = stepScope.getScore();
        lateScoreIndex = (lateScoreIndex + 1) % lateAnnealingSize;
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        previousScores = null;
        lateScoreIndex = -1;
    }

}
