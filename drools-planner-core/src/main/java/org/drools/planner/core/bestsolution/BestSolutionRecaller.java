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

package org.drools.planner.core.bestsolution;

import org.drools.planner.core.Solver;
import org.drools.planner.core.event.SolverEventSupport;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolverScope;
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
        boolean workingSolutionInitialized = solverScope.isWorkingSolutionInitialized();
        Score startingInitializedScore;
        if (workingSolutionInitialized) {
            startingInitializedScore = solverScope.calculateScore();
        } else {
            startingInitializedScore = null;
        }
        solverScope.setStartingInitializedScore(startingInitializedScore);
        solverScope.setBestScore(startingInitializedScore);
        // The original bestSolution might be the final bestSolution and should have an accurate Score
        solverScope.getBestSolution().setScore(startingInitializedScore);
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        if (!stepScope.isSolutionInitialized()) {
            return;
        }
        AbstractSolverPhaseScope solverPhaseScope = stepScope.getSolverPhaseScope();
        DefaultSolverScope solverScope = solverPhaseScope.getSolverScope();
        Score newScore = stepScope.getScore();
        Score bestScore = solverScope.getBestScore();
        boolean bestScoreImproved;
        if (bestScore == null) {
            bestScoreImproved = true;
            solverScope.setStartingInitializedScore(newScore);
        } else {
            bestScoreImproved = newScore.compareTo(bestScore) > 0;
        }
        stepScope.setBestScoreImproved(bestScoreImproved);
        if (bestScoreImproved) {
            solverPhaseScope.setBestSolutionStepIndex(stepScope.getStepIndex());
            Solution newBestSolution = stepScope.createOrGetClonedSolution();
            updateBestSolution(solverScope, newBestSolution);
        } else if (assertBestScoreIsUnmodified) {
            solverScope.assertScore(solverScope.getBestSolution());
        }
    }

    public void updateBestSolution(DefaultSolverScope solverScope, Solution newBestSolution) {
        solverScope.setBestSolution(newBestSolution);
        solverScope.setBestScore(newBestSolution.getScore());
        solverEventSupport.fireBestSolutionChanged(newBestSolution);
    }

}
