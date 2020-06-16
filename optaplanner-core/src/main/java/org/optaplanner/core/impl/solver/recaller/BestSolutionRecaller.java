/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remembers the {@link PlanningSolution best solution} that a {@link Solver} encounters.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class BestSolutionRecaller<Solution_> extends PhaseLifecycleListenerAdapter<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected boolean assertInitialScoreFromScratch = false;
    protected boolean assertShadowVariablesAreNotStale = false;
    protected boolean assertBestScoreIsUnmodified = false;

    protected SolverEventSupport<Solution_> solverEventSupport;

    public void setAssertInitialScoreFromScratch(boolean assertInitialScoreFromScratch) {
        this.assertInitialScoreFromScratch = assertInitialScoreFromScratch;
    }

    public void setAssertShadowVariablesAreNotStale(boolean assertShadowVariablesAreNotStale) {
        this.assertShadowVariablesAreNotStale = assertShadowVariablesAreNotStale;
    }

    public void setAssertBestScoreIsUnmodified(boolean assertBestScoreIsUnmodified) {
        this.assertBestScoreIsUnmodified = assertBestScoreIsUnmodified;
    }

    public void setSolverEventSupport(SolverEventSupport<Solution_> solverEventSupport) {
        this.solverEventSupport = solverEventSupport;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        // Starting bestSolution is already set by Solver.solve(Solution)
        InnerScoreDirector<Solution_> scoreDirector = solverScope.getScoreDirector();
        Score score = scoreDirector.calculateScore();
        solverScope.setBestScore(score);
        solverScope.setBestSolutionTimeMillis(System.currentTimeMillis());
        // The original bestSolution might be the final bestSolution and should have an accurate Score
        solverScope.getSolutionDescriptor().setScore(solverScope.getBestSolution(), score);
        if (score.isSolutionInitialized()) {
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

    public void processWorkingSolutionDuringStep(AbstractStepScope<Solution_> stepScope) {
        AbstractPhaseScope<Solution_> phaseScope = stepScope.getPhaseScope();
        Score score = stepScope.getScore();
        SolverScope<Solution_> solverScope = phaseScope.getSolverScope();
        boolean bestScoreImproved = score.compareTo(solverScope.getBestScore()) > 0;
        stepScope.setBestScoreImproved(bestScoreImproved);
        if (bestScoreImproved) {
            phaseScope.setBestSolutionStepIndex(stepScope.getStepIndex());
            Solution_ newBestSolution = stepScope.createOrGetClonedSolution();
            updateBestSolution(solverScope, score, newBestSolution);
        } else if (assertBestScoreIsUnmodified) {
            solverScope.assertScoreFromScratch(solverScope.getBestSolution());
        }
    }

    public void processWorkingSolutionDuringMove(Score score, AbstractStepScope<Solution_> stepScope) {
        AbstractPhaseScope<Solution_> phaseScope = stepScope.getPhaseScope();
        SolverScope<Solution_> solverScope = phaseScope.getSolverScope();
        boolean bestScoreImproved = score.compareTo(solverScope.getBestScore()) > 0;
        // The method processWorkingSolutionDuringMove() is called 0..* times
        // stepScope.getBestScoreImproved() is initialized on false before the first call here
        if (bestScoreImproved) {
            stepScope.setBestScoreImproved(bestScoreImproved);
        }
        if (bestScoreImproved) {
            phaseScope.setBestSolutionStepIndex(stepScope.getStepIndex());
            Solution_ newBestSolution = solverScope.getScoreDirector().cloneWorkingSolution();
            updateBestSolution(solverScope, score, newBestSolution);
        } else if (assertBestScoreIsUnmodified) {
            solverScope.assertScoreFromScratch(solverScope.getBestSolution());
        }
    }

    public void updateBestSolution(SolverScope<Solution_> solverScope) {
        Solution_ newBestSolution = solverScope.getScoreDirector().cloneWorkingSolution();
        Score newBestScore = solverScope.getSolutionDescriptor().getScore(newBestSolution);
        updateBestSolution(solverScope, newBestScore, newBestSolution);
    }

    protected void updateBestSolution(SolverScope<Solution_> solverScope, Score bestScore,
            Solution_ bestSolution) {
        if (bestScore.isSolutionInitialized()) {
            if (!solverScope.isBestSolutionInitialized()) {
                solverScope.setStartingInitializedScore(bestScore);
            }
        }
        solverScope.setBestSolution(bestSolution);
        solverScope.setBestScore(bestScore);
        solverScope.setBestSolutionTimeMillis(System.currentTimeMillis());
        solverEventSupport.fireBestSolutionChanged(solverScope, bestSolution);
    }

}
