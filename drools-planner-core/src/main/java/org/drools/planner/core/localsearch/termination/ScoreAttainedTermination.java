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

import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.score.Score;

/**
 * @author Geoffrey De Smet
 */
public class ScoreAttainedTermination extends AbstractTermination {

    private Score scoreAttained;

    public void setScoreAttained(Score scoreAttained) {
        this.scoreAttained = scoreAttained;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isTerminated(LocalSearchStepScope localSearchStepScope) {
        Score bestScore = localSearchStepScope.getLocalSearchSolverScope().getBestScore();
        return bestScore.compareTo(scoreAttained) >= 0;
    }

    public double calculateTimeGradient(LocalSearchStepScope localSearchStepScope) {
        LocalSearchSolverScope localSearchSolverScope = localSearchStepScope.getLocalSearchSolverScope();
        Score startingScore = localSearchSolverScope.getStartingScore();
        Score stepScore = localSearchSolverScope.getLastCompletedLocalSearchStepScope().getScore();
        return localSearchSolverScope.getScoreDefinition()
                .calculateTimeGradient(startingScore, scoreAttained, stepScore);
    }

}
