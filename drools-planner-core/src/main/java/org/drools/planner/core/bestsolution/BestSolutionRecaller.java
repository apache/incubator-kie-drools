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
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.solver.event.SolverLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A BestSolutionRecaller remembers the best solution that a {@link Solver} encounters.
 */
public class BestSolutionRecaller implements SolverLifecycleListener {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverEventSupport solverEventSupport;

    public void setSolverEventSupport(SolverEventSupport solverEventSupport) {
        this.solverEventSupport = solverEventSupport;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solvingStarted(DefaultSolverScope solverScope) {
        boolean workingSolutionInitialized = solverScope.isWorkingSolutionInitialized();
        Score startingScore;
        Solution bestSolution;
        if (workingSolutionInitialized) {
            startingScore = solverScope.calculateScoreFromWorkingMemory();
            bestSolution = solverScope.getWorkingSolution().cloneSolution();
        } else {
            startingScore = null;
            bestSolution = null;
        }
        solverScope.setStartingScore(startingScore);
        solverScope.setBestSolution(bestSolution);
        solverScope.setBestScore(startingScore);
    }

    public void extractBestSolution(AbstractStepScope stepScope) {
        if (!stepScope.isSolutionInitialized()) {
            return;
        }
        AbstractSolverPhaseScope solverPhaseScope = stepScope.getSolverPhaseScope();
        Score newScore = stepScope.getScore();
        Score bestScore = solverPhaseScope.getBestScore();
        boolean bestScoreImproved = bestScore == null || newScore.compareTo(bestScore) > 0;
        stepScope.setBestScoreImproved(bestScoreImproved);
        if (bestScoreImproved) {
            solverPhaseScope.setBestSolutionStepIndex(stepScope.getStepIndex());
            Solution newBestSolution = stepScope.createOrGetClonedSolution();
            DefaultSolverScope solverScope = solverPhaseScope.getSolverScope();
            solverScope.setBestSolution(newBestSolution);
            solverScope.setBestScore(newBestSolution.getScore());
            solverEventSupport.fireBestSolutionChanged(newBestSolution);
        }
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
    }

}
