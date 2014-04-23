/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.impl.phase.scope.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.score.definition.FeasibilityScoreDefinition;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class BestScoreFeasibleTermination extends AbstractTermination {

    @Override
    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        FeasibilityScore bestScore = (FeasibilityScore) solverScope.getBestScore();
        return bestScore.isFeasible();
    }

    @Override
    public boolean isPhaseTerminated(AbstractSolverPhaseScope phaseScope) {
        FeasibilityScore bestScore = (FeasibilityScore) phaseScope.getBestScore();
        return bestScore.isFeasible();
    }

    @Override
    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        FeasibilityScoreDefinition scoreDefinition = (FeasibilityScoreDefinition) solverScope.getScoreDefinition();
        return scoreDefinition.calculateFeasibilityTimeGradient(
                (FeasibilityScore) solverScope.getStartingInitializedScore(), (FeasibilityScore) solverScope.getBestScore());
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractSolverPhaseScope phaseScope) {
        FeasibilityScoreDefinition scoreDefinition = (FeasibilityScoreDefinition) phaseScope.getScoreDefinition();
        return scoreDefinition.calculateFeasibilityTimeGradient(
                (FeasibilityScore) phaseScope.getStartingScore(), (FeasibilityScore) phaseScope.getBestScore());
    }

}
