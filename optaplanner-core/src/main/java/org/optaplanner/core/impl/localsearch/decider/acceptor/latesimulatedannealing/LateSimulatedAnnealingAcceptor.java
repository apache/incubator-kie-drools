/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.latesimulatedannealing;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.ScoreUtils;

@Deprecated
public class LateSimulatedAnnealingAcceptor extends AbstractAcceptor {

    protected int lateSimulatedAnnealingSize = -1;

    protected Score[] previousScores;
    protected int lateScoreIndex = -1;

    public void setLateSimulatedAnnealingSize(int lateSimulatedAnnealingSize) {
        this.lateSimulatedAnnealingSize = lateSimulatedAnnealingSize;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        validate();
        previousScores = new Score[lateSimulatedAnnealingSize];
        Score initialScore = phaseScope.getBestScore();
        for (int i = 0; i < previousScores.length; i++) {
            previousScores[i] = initialScore;
        }
        lateScoreIndex = 0;
    }

    private void validate() {
        if (lateSimulatedAnnealingSize <= 0) {
            throw new IllegalArgumentException("The lateSimulatedAnnealingSize (" + lateSimulatedAnnealingSize
                    + ") cannot be negative or zero.");
        }
    }

    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        Score moveScore = moveScope.getScore();
        Score lastStepScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
        if (moveScore.compareTo(lastStepScore) >= 0) {
            return true;
        }
        Score lateScore = previousScores[lateScoreIndex];
        Score bestScore = moveScope.getStepScope().getPhaseScope().getBestScore();
        Score moveScoreDifference = bestScore.subtract(moveScore);
        double[] moveScoreDifferenceLevels = ScoreUtils.extractLevelDoubles(moveScoreDifference);
        Score lateScoreDifference = bestScore.subtract(lateScore);
        double[] lateScoreDifferenceLevels = ScoreUtils.extractLevelDoubles(lateScoreDifference);
        double acceptChance = 1.0;
        for (int i = 0; i < moveScoreDifferenceLevels.length; i++) {
            double moveScoreDifferenceLevel = moveScoreDifferenceLevels[i];
            double lateScoreDifferenceLevel = lateScoreDifferenceLevels[i];
            double acceptChanceLevel;
            if (moveScoreDifferenceLevel <= 0.0) {
                // In this level, moveScore is better than the bestScore, so do not disrupt the acceptChance
                acceptChanceLevel = 1.0;
            } else {
                acceptChanceLevel = Math.exp(-moveScoreDifferenceLevel / lateScoreDifferenceLevel);
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
        lateScoreIndex = (lateScoreIndex + 1) % lateSimulatedAnnealingSize;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        previousScores = null;
        lateScoreIndex = -1;
    }

}
