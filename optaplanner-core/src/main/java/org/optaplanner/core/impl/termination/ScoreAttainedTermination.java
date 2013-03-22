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

package org.optaplanner.core.impl.termination;

import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class ScoreAttainedTermination extends AbstractTermination {

    private Score scoreAttained;

    public void setScoreAttained(Score scoreAttained) {
        this.scoreAttained = scoreAttained;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        return isTerminated(solverScope.isBestSolutionInitialized(), solverScope.getBestScore());
    }

    public boolean isPhaseTerminated(AbstractSolverPhaseScope phaseScope) {
        return isTerminated(phaseScope.isBestSolutionInitialized(), phaseScope.getBestScore());
    }

    private boolean isTerminated(boolean bestSolutionInitialized, Score bestScore) {
        return bestSolutionInitialized && bestScore.compareTo(scoreAttained) >= 0;
    }

    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        Score startingInitializedScore = solverScope.getStartingInitializedScore();
        Score bestScore = solverScope.getBestScore();
        return solverScope.getScoreDefinition().calculateTimeGradient(
                startingInitializedScore, scoreAttained, bestScore);
    }

    public double calculatePhaseTimeGradient(AbstractSolverPhaseScope phaseScope) {
        Score startingInitializedScore = phaseScope.getStartingScore();
        Score bestScore = phaseScope.getBestScore();
        return phaseScope.getScoreDefinition().calculateTimeGradient(startingInitializedScore, scoreAttained, bestScore);
    }

}
