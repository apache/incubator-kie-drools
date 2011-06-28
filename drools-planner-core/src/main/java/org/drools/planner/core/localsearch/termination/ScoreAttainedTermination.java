/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.localsearch.termination;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.solver.AbstractSolverPhaseScope;
import org.drools.planner.core.solver.AbstractStepScope;

public class ScoreAttainedTermination extends AbstractTermination {

    private Score scoreAttained;

    public void setScoreAttained(Score scoreAttained) {
        this.scoreAttained = scoreAttained;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isTerminated(AbstractStepScope stepScope) {
        Score bestScore = stepScope.getSolverPhaseScope().getBestScore();
        return bestScore.compareTo(scoreAttained) >= 0;
    }

    public double calculateTimeGradient(AbstractStepScope stepScope) {
        AbstractSolverPhaseScope solverPhaseScope = stepScope.getSolverPhaseScope();
        Score startingScore = solverPhaseScope.getStartingScore();
        Score lastCompletedStepScore = solverPhaseScope.getLastCompletedStepScope().getScore();
        return solverPhaseScope.getScoreDefinition()
                .calculateTimeGradient(startingScore, scoreAttained, lastCompletedStepScore);
    }

}
