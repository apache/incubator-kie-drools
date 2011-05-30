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

import org.drools.RuleBase;
import org.drools.planner.core.bestsolution.BestSolutionRecaller;
import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.calculator.ScoreCalculator;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.AbstractSolver;
import org.drools.planner.core.solver.AbstractSolverScope;

/**
 * Default implementation of {@link BruteForceSolver}.
 */
public class DefaultBruteForceSolver extends AbstractSolver implements BruteForceSolver {

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

        BruteForceSolutionIterator bruteForceSolutionIterator
                = new BruteForceSolutionIterator(bruteForceSolverScope);

        BruteForceStepScope bruteForceStepScope = createNextStepScope(bruteForceSolverScope, null);
        for (; bruteForceSolutionIterator.hasNext(); bruteForceSolutionIterator.next()) {
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
//        bruteForceSolverScope.setLastCompletedLocalSearchStepScope(completedBruteForceStepScope); TODO add back in
        BruteForceStepScope bruteForceStepScope = new BruteForceStepScope(bruteForceSolverScope);
        bruteForceStepScope.setStepIndex(completedBruteForceStepScope.getStepIndex() + 1);
        return bruteForceStepScope;
    }

    private LocalSearchStepScope createNextStepScope(LocalSearchSolverScope localSearchSolverScope, LocalSearchStepScope completedLocalSearchStepScope) {
        if (completedLocalSearchStepScope == null) {
            completedLocalSearchStepScope = new LocalSearchStepScope(localSearchSolverScope);
            completedLocalSearchStepScope.setScore(localSearchSolverScope.getStartingScore());
            completedLocalSearchStepScope.setStepIndex(-1);
            completedLocalSearchStepScope.setTimeGradient(0.0);
        }
        localSearchSolverScope.setLastCompletedLocalSearchStepScope(completedLocalSearchStepScope);
        LocalSearchStepScope localSearchStepScope = new LocalSearchStepScope(localSearchSolverScope);
        localSearchStepScope.setStepIndex(completedLocalSearchStepScope.getStepIndex() + 1);
        return localSearchStepScope;
    }

    public void solvingStarted(BruteForceSolverScope bruteForceSolverScope) {
        super.solvingStarted(bruteForceSolverScope);
    }

    public void solvingEnded(BruteForceSolverScope bruteForceSolverScope) {
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
