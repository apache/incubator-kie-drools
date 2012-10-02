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

package org.drools.planner.core.localsearch.decider.acceptor.lateacceptance;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.LocalSolverMoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.AbstractAcceptor;
import org.drools.planner.core.score.Score;

public class LateAcceptanceAcceptor extends AbstractAcceptor {

    protected int lateAcceptanceSize = -1;

    protected Score[] previousScores;
    protected int lateScoreIndex = -1;

    public void setLateAcceptanceSize(int lateAcceptanceSize) {
        this.lateAcceptanceSize = lateAcceptanceSize;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseStarted(localSearchSolverPhaseScope);
        validate();
        previousScores = new Score[lateAcceptanceSize];
        Score initialScore = localSearchSolverPhaseScope.getBestScore();
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
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseEnded(localSearchSolverPhaseScope);
        previousScores = null;
        lateScoreIndex = -1;
    }

    public boolean isAccepted(LocalSolverMoveScope moveScope) {
        moveScope.getLocalSearchStepScope().getStepIndex();
        Score score = moveScope.getScore();
        Score lateScore = previousScores[lateScoreIndex];
        return score.compareTo(lateScore) >= 0;
    }

    @Override
    public void stepEnded(LocalSearchStepScope localSearchStepScope) {
        super.stepEnded(localSearchStepScope);
        previousScores[lateScoreIndex] = localSearchStepScope.getScore();
        lateScoreIndex = (lateScoreIndex + 1) % lateAcceptanceSize;
    }

}
