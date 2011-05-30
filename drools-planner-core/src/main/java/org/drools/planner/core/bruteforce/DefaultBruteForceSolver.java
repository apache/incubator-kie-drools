/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.bruteforce;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.solver.AbstractSolver;
import org.drools.planner.core.solver.AbstractSolverScope;

/**
 * Default implementation of {@link BruteForceSolver}.
 */
public class DefaultBruteForceSolver extends AbstractSolver implements BruteForceSolver {

    protected BruteForceSolutionIterator bruteForceSolutionIterator = new BruteForceSolutionIterator();

    protected BruteForceSolverScope bruteForceSolverScope = new BruteForceSolverScope();

    @Override
    public AbstractSolverScope getAbstractSolverScope() {
        return bruteForceSolverScope;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    protected void solveImplementation() {
        BruteForceSolverScope bruteForceSolverScope = this.bruteForceSolverScope;
        solvingStarted(bruteForceSolverScope);

        BruteForceStepScope bruteForceStepScope = createNextStepScope(bruteForceSolverScope, null);
        while (bruteForceSolutionIterator.hasNext()) {
            bruteForceSolutionIterator.next();
            Score score = bruteForceSolverScope.calculateScoreFromWorkingMemory();
            bruteForceStepScope.setScore(score);
            bestSolutionRecaller.stepTaken(bruteForceStepScope);
            logger.debug("Step index ({}), time spend ({}), score ({}), {} best score ({}).",
                    new Object[]{bruteForceStepScope.getStepIndex(), bruteForceSolverScope.calculateTimeMillisSpend(),
                            bruteForceStepScope.getScore(), (bruteForceStepScope.getBestScoreImproved() ? "new" : "   "),
                            bruteForceSolverScope.getBestScore()});
            bruteForceStepScope = createNextStepScope(bruteForceSolverScope, bruteForceStepScope);
        }
        solvingEnded(bruteForceSolverScope);
    }

    private BruteForceStepScope createNextStepScope(BruteForceSolverScope bruteForceSolverScope, BruteForceStepScope completedBruteForceStepScope) {
        if (completedBruteForceStepScope == null) {
            completedBruteForceStepScope = new BruteForceStepScope(bruteForceSolverScope);
            completedBruteForceStepScope.setScore(bruteForceSolverScope.getStartingScore());
            completedBruteForceStepScope.setStepIndex(-1);
        }
        bruteForceSolverScope.setLastCompletedBruteForceStepScope(completedBruteForceStepScope);
        BruteForceStepScope bruteForceStepScope = new BruteForceStepScope(bruteForceSolverScope);
        bruteForceStepScope.setStepIndex(completedBruteForceStepScope.getStepIndex() + 1);
        return bruteForceStepScope;
    }

    public void solvingStarted(BruteForceSolverScope bruteForceSolverScope) {
        super.solvingStarted(bruteForceSolverScope);
        bruteForceSolutionIterator.solvingStarted(bruteForceSolverScope);
    }

    public void solvingEnded(BruteForceSolverScope bruteForceSolverScope) {
        bruteForceSolutionIterator.solvingEnded(bruteForceSolverScope);
        bestSolutionRecaller.solvingEnded(bruteForceSolverScope);
        long timeMillisSpend = bruteForceSolverScope.calculateTimeMillisSpend();
        long averageCalculateCountPerSecond = bruteForceSolverScope.getCalculateCount() * 1000L / timeMillisSpend;
        logger.info("Solved with time spend ({}) for best score ({})"
                + " with average calculate count per second ({}).", new Object[]{
                timeMillisSpend,
                bruteForceSolverScope.getBestScore(),
                averageCalculateCountPerSecond
        });
    }

}
