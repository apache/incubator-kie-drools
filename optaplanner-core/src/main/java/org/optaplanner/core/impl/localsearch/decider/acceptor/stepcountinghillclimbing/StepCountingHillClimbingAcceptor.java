/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.stepcountinghillclimbing;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class StepCountingHillClimbingAcceptor extends AbstractAcceptor {

    protected int stepCountingHillClimbingSize = -1;

    protected Score thresholdScore;
    protected int stepCount = -1;

    public void setStepCountingHillClimbingSize(int stepCountingHillClimbingSize) {
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        validate();
        thresholdScore = phaseScope.getBestScore();
        stepCount = 0;
    }

    private void validate() {
        if (stepCountingHillClimbingSize <= 0) {
            throw new IllegalArgumentException("The stepCountingHillClimbingSize (" + stepCountingHillClimbingSize
                    + ") cannot be negative or zero.");
        }
    }

    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        Score lastStepScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(lastStepScore) >= 0) {
            return true;
        }
        return moveScore.compareTo(thresholdScore) >= 0;
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        stepCount++;
        if (stepCount >= stepCountingHillClimbingSize) {
            thresholdScore = stepScope.getScore();
            stepCount = 0;
        }
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        thresholdScore = null;
        stepCount = -1;
    }

}
