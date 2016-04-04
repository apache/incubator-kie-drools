/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.lateacceptance;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class LateAcceptanceAcceptor extends AbstractAcceptor {

    protected int lateAcceptanceSize = -1;
    protected boolean hillClimbingEnabled = true;

    protected Score[] previousScores;
    protected int lateScoreIndex = -1;

    public void setLateAcceptanceSize(int lateAcceptanceSize) {
        this.lateAcceptanceSize = lateAcceptanceSize;
    }

    public void setHillClimbingEnabled(boolean hillClimbingEnabled) {
        this.hillClimbingEnabled = hillClimbingEnabled;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        validate();
        previousScores = new Score[lateAcceptanceSize];
        Score initialScore = phaseScope.getBestScore();
        for (int i = 0; i < previousScores.length; i++) {
            previousScores[i] = initialScore;
        }
        lateScoreIndex = 0;
    }

    private void validate() {
        if (lateAcceptanceSize <= 0) {
            throw new IllegalArgumentException("The lateAcceptanceSize (" + lateAcceptanceSize
                    + ") cannot be negative or zero.");
        }
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        Score moveScore = moveScope.getScore();
        Score lateScore = previousScores[lateScoreIndex];
        if (moveScore.compareTo(lateScore) >= 0) {
            return true;
        }
        if (hillClimbingEnabled) {
            Score lastStepScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
            if (moveScore.compareTo(lastStepScore) >= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        previousScores[lateScoreIndex] = stepScope.getScore();
        lateScoreIndex = (lateScoreIndex + 1) % lateAcceptanceSize;
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        previousScores = null;
        lateScoreIndex = -1;
    }

}
