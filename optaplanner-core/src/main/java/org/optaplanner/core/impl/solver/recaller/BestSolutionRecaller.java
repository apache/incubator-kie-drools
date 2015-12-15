/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.recaller;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A BestSolutionRecaller remembers the best solution that a {@link Solver} encounters.
 */
public class BestSolutionRecaller extends PhaseLifecycleListenerAdapter {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected boolean assertInitialScoreFromScratch = false;
    protected boolean assertShadowVariablesAreNotStale = false;
    protected boolean assertBestScoreIsUnmodified = false;

    protected SolverEventSupport solverEventSupport;

    public void setAssertInitialScoreFromScratch(boolean assertInitialScoreFromScratch) {
        this.assertInitialScoreFromScratch = assertInitialScoreFromScratch;
    }

    public void setAssertShadowVariablesAreNotStale(boolean assertShadowVariablesAreNotStale) {
        this.assertShadowVariablesAreNotStale = assertShadowVariablesAreNotStale;
    }

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
        // Starting bestSolution is already set by Solver.solve(Solution)
        InnerScoreDirector scoreDirector = solverScope.getScoreDirector();
        int uninitializedVariableCount = scoreDirector.countWorkingSolutionUninitializedVariables();
        solverScope.setBestUninitializedVariableCount(uninitializedVariableCount);
        Score score = scoreDirector.calculateScore();
        solverScope.setBestScore(score);
        solverScope.setBestSolutionTimeMillis(System.currentTimeMillis());
        // The original bestSolution might be the final bestSolution and should have an accurate Score
        solverScope.getBestSolution().setScore(score);
        if (uninitializedVariableCount == 0) {
            solverScope.setStartingInitializedScore(score);
        } else {
            solverScope.setStartingInitializedScore(null);
        }
        if (assertInitialScoreFromScratch) {
            scoreDirector.assertWorkingScoreFromScratch(score, "Initial score calculated");
        }
        if (assertShadowVariablesAreNotStale) {
            scoreDirector.assertShadowVariablesAreNotStale(score, "Initial score calculated");
        }
    }

    public void processWorkingSolutionDuringStep(AbstractStepScope stepScope) {
        AbstractPhaseScope phaseScope = stepScope.getPhaseScope();
        int uninitializedVariableCount = stepScope.getUninitializedVariableCount();
        Score score = stepScope.getScore();
        DefaultSolverScope solverScope = phaseScope.getSolverScope();
        int bestUninitializedVariableCount = solverScope.getBestUninitializedVariableCount();
        Score bestScore = solverScope.getBestScore();
        boolean bestScoreImproved;
        if (uninitializedVariableCount == bestUninitializedVariableCount) {
            bestScoreImproved = score.compareTo(bestScore) > 0;
        } else {
            bestScoreImproved = uninitializedVariableCount < bestUninitializedVariableCount;
        }
        stepScope.setBestScoreImproved(bestScoreImproved);
        if (bestScoreImproved) {
            phaseScope.setBestSolutionStepIndex(stepScope.getStepIndex());
            Solution newBestSolution = stepScope.createOrGetClonedSolution();
            updateBestSolution(solverScope, newBestSolution, uninitializedVariableCount);
        } else if (assertBestScoreIsUnmodified) {
            solverScope.assertScoreFromScratch(solverScope.getBestSolution());
        }
    }

    public void processWorkingSolutionDuringMove(
            int uninitializedVariableCount, Score score, AbstractStepScope stepScope) {
        AbstractPhaseScope phaseScope = stepScope.getPhaseScope();
        DefaultSolverScope solverScope = phaseScope.getSolverScope();
        int bestUninitializedVariableCount = solverScope.getBestUninitializedVariableCount();
        Score bestScore = solverScope.getBestScore();
        boolean bestScoreImproved;
        if (uninitializedVariableCount == bestUninitializedVariableCount) {
            bestScoreImproved = score.compareTo(bestScore) > 0;
        } else {
            bestScoreImproved = uninitializedVariableCount < bestUninitializedVariableCount;
        }
        // The method processWorkingSolutionDuringMove() is called 0..* times
        // stepScope.getBestScoreImproved() is initialized on false before the first call here
        if (bestScoreImproved) {
            stepScope.setBestScoreImproved(bestScoreImproved);
        }
        if (bestScoreImproved) {
            phaseScope.setBestSolutionStepIndex(stepScope.getStepIndex());
            Solution newBestSolution = solverScope.getScoreDirector().cloneWorkingSolution();
            updateBestSolution(solverScope, newBestSolution, uninitializedVariableCount);
        } else if (assertBestScoreIsUnmodified) {
            solverScope.assertScoreFromScratch(solverScope.getBestSolution());
        }
    }

    public void updateBestSolution(DefaultSolverScope solverScope, Solution solution, int uninitializedVariableCount) {
        if (uninitializedVariableCount == 0) {
            if (!solverScope.isBestSolutionInitialized()) {
                solverScope.setStartingInitializedScore(solution.getScore());
            }
        }
        solverScope.setBestUninitializedVariableCount(uninitializedVariableCount);
        solverScope.setBestSolution(solution);
        solverScope.setBestScore(solution.getScore());
        solverScope.setBestSolutionTimeMillis(System.currentTimeMillis());
        solverEventSupport.fireBestSolutionChanged(solution, uninitializedVariableCount);
    }

}
