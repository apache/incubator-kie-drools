/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.bruteforce;

import org.drools.planner.core.bruteforce.event.BruteForceSolverPhaseLifecycleListener;
import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * Default implementation of {@link BruteForceSolverPhase}.
 */
public class DefaultBruteForceSolverPhase extends AbstractSolverPhase
        implements BruteForceSolverPhase, BruteForceSolverPhaseLifecycleListener {

    protected BruteForceEntityWalker bruteForceEntityWalker;

    public void setBruteForceEntityWalker(BruteForceEntityWalker bruteForceEntityWalker) {
        this.bruteForceEntityWalker = bruteForceEntityWalker;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        BruteForceSolverPhaseScope bruteForceSolverPhaseScope = new BruteForceSolverPhaseScope(solverScope);
        phaseStarted(bruteForceSolverPhaseScope);

        BruteForceStepScope bruteForceStepScope = createNextStepScope(bruteForceSolverPhaseScope, null);
        while (!termination.isPhaseTerminated(bruteForceSolverPhaseScope) && bruteForceEntityWalker.hasWalk()) {
            bruteForceEntityWalker.walk();
            Score score = bruteForceSolverPhaseScope.calculateScore();
            bruteForceStepScope.setScore(score);
            stepTaken(bruteForceStepScope);
            bruteForceStepScope = createNextStepScope(bruteForceSolverPhaseScope, bruteForceStepScope);
        }
        phaseEnded(bruteForceSolverPhaseScope);
    }

    private BruteForceStepScope createNextStepScope(BruteForceSolverPhaseScope bruteForceSolverPhaseScope, BruteForceStepScope completedBruteForceStepScope) {
        if (completedBruteForceStepScope == null) {
            completedBruteForceStepScope = new BruteForceStepScope(bruteForceSolverPhaseScope);
            completedBruteForceStepScope.setScore(bruteForceSolverPhaseScope.getStartingScore());
            completedBruteForceStepScope.setStepIndex(-1);
        }
        bruteForceSolverPhaseScope.setLastCompletedBruteForceStepScope(completedBruteForceStepScope);
        BruteForceStepScope bruteForceStepScope = new BruteForceStepScope(bruteForceSolverPhaseScope);
        bruteForceStepScope.setStepIndex(completedBruteForceStepScope.getStepIndex() + 1);
        bruteForceStepScope.setSolutionInitialized(true);
        return bruteForceStepScope;
    }

    public void phaseStarted(BruteForceSolverPhaseScope bruteForceSolverPhaseScope) {
        super.phaseStarted(bruteForceSolverPhaseScope);
        bruteForceEntityWalker.phaseStarted(bruteForceSolverPhaseScope);
    }

    public void stepTaken(BruteForceStepScope bruteForceStepScope) {
        super.stepTaken(bruteForceStepScope);
        bruteForceEntityWalker.stepTaken(bruteForceStepScope);
        BruteForceSolverPhaseScope bruteForceSolverPhaseScope = bruteForceStepScope.getBruteForceSolverPhaseScope();
        // TODO The steps are too fine, so debug log is too much. Yet we still want some debug indication
        logger.debug("    Step index ({}), time spend ({}), score ({}), {} best score ({}).",
                new Object[]{bruteForceStepScope.getStepIndex(),
                        bruteForceSolverPhaseScope.calculateSolverTimeMillisSpend(),
                        bruteForceStepScope.getScore(), (bruteForceStepScope.getBestScoreImproved() ? "new" : "   "),
                        bruteForceSolverPhaseScope.getBestScore()});
    }

    public void phaseEnded(BruteForceSolverPhaseScope bruteForceSolverPhaseScope) {
        super.phaseEnded(bruteForceSolverPhaseScope);
        bruteForceEntityWalker.phaseEnded(bruteForceSolverPhaseScope);
        logger.info("Phase bruteForce ended: step total ({}), time spend ({}), best score ({}).",
                new Object[]{bruteForceSolverPhaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                bruteForceSolverPhaseScope.calculateSolverTimeMillisSpend(),
                bruteForceSolverPhaseScope.getBestScore()});
    }

}
