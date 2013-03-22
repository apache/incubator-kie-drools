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

package org.optaplanner.core.impl.bestsolution;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.event.SolverEventSupport;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A BestSolutionRecaller remembers the best solution that a {@link Solver} encounters.
 */
public class BestSolutionRecaller extends SolverPhaseLifecycleListenerAdapter {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected boolean assertBestScoreIsUnmodified = false;

    protected SolverEventSupport solverEventSupport;

    public void setAssertBestScoreIsUnmodified(boolean assertBestScoreIsUnmodified) {
        this.assertBestScoreIsUnmodified = assertBestScoreIsUnmodified;
    }

    public void setSolverEventSupport(SolverEventSupport solverEventSupport) {
        this.solverEventSupport = solverEventSupport;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        // Starting bestSolution is already set by Solver.setPlanningProblem()
        int uninitializedVariableCount = solverScope.getScoreDirector().countWorkingSolutionUninitializedVariables();
        solverScope.setBestUninitializedVariableCount(uninitializedVariableCount);
        Score score = solverScope.calculateScore();
        solverScope.setBestScore(score);
        // The original bestSolution might be the final bestSolution and should have an accurate Score
        solverScope.getBestSolution().setScore(score);
        if (uninitializedVariableCount == 0) {
            solverScope.setStartingInitializedScore(score);
        }
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        if (stepScope.isBestSolutionCloningDelayed()) {
            return;
        }
        AbstractSolverPhaseScope phaseScope = stepScope.getPhaseScope();
        DefaultSolverScope solverScope = phaseScope.getSolverScope();
        int newUninitializedVariableCount = stepScope.getUninitializedVariableCount();
        Score newScore = stepScope.getScore();
        int bestUninitializedVariableCount = solverScope.getBestUninitializedVariableCount();
        Score bestScore = solverScope.getBestScore();
        boolean bestScoreImproved;
        if (newUninitializedVariableCount == bestUninitializedVariableCount) {
            bestScoreImproved = newScore.compareTo(bestScore) > 0;
        } else {
            bestScoreImproved = newUninitializedVariableCount < bestUninitializedVariableCount;
        }
        stepScope.setBestScoreImproved(bestScoreImproved);
        if (bestScoreImproved) {
            phaseScope.setBestSolutionStepIndex(stepScope.getStepIndex());
            Solution newBestSolution = stepScope.createOrGetClonedSolution();
            updateBestSolution(solverScope, newBestSolution, newUninitializedVariableCount);
        } else if (assertBestScoreIsUnmodified) {
            solverScope.assertScore(solverScope.getBestSolution());
        }
    }

    public void updateBestSolution(DefaultSolverScope solverScope, Solution solution, int uninitializedVariableCount) {
        if (uninitializedVariableCount == 0) {
            if (!solverScope.isBestSolutionInitialized()) {
                solverScope.setStartingInitializedScore(solution.getScore());
            }
        } else {
            solverScope.setStartingInitializedScore(null);
        }
        solverScope.setBestUninitializedVariableCount(uninitializedVariableCount);
        solverScope.setBestSolution(solution);
        solverScope.setBestScore(solution.getScore());
        solverEventSupport.fireBestSolutionChanged(solution);
    }

}
