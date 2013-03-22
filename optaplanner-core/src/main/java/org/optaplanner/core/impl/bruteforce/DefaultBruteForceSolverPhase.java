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

package org.optaplanner.core.impl.bruteforce;

import org.optaplanner.core.impl.bruteforce.event.BruteForceSolverPhaseLifecycleListener;
import org.optaplanner.core.impl.bruteforce.scope.BruteForceSolverPhaseScope;
import org.optaplanner.core.impl.bruteforce.scope.BruteForceStepScope;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

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

        BruteForceStepScope stepScope = createNextStepScope(bruteForceSolverPhaseScope, null);
        while (!termination.isPhaseTerminated(bruteForceSolverPhaseScope) && bruteForceEntityWalker.hasWalk()) {
            stepStarted(stepScope);
            bruteForceEntityWalker.walk();
            Score score = bruteForceSolverPhaseScope.calculateScore();
            stepScope.setScore(score);
            stepEnded(stepScope);
            stepScope = createNextStepScope(bruteForceSolverPhaseScope, stepScope);
        }
        phaseEnded(bruteForceSolverPhaseScope);
    }

    private BruteForceStepScope createNextStepScope(BruteForceSolverPhaseScope phaseScope,
            BruteForceStepScope completedStepScope) {
        if (completedStepScope == null) {
            completedStepScope = new BruteForceStepScope(phaseScope);
            completedStepScope.setScore(phaseScope.getStartingScore());
            completedStepScope.setStepIndex(-1);
        }
        phaseScope.setLastCompletedStepScope(completedStepScope);
        BruteForceStepScope stepScope = new BruteForceStepScope(phaseScope);
        stepScope.setStepIndex(completedStepScope.getStepIndex() + 1);
        return stepScope;
    }

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        // TODO hook the walker up in the solver lifecycle (this will probably be fixed by selector unification)
//        bruteForceEntityWalker.solvingStarted(solverScope);
    }

    public void phaseStarted(BruteForceSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        bruteForceEntityWalker.phaseStarted(phaseScope);
    }

    public void stepStarted(BruteForceStepScope stepScope) {
        super.stepStarted(stepScope);
    }

    public void stepEnded(BruteForceStepScope stepScope) {
        super.stepEnded(stepScope);
        bruteForceEntityWalker.stepEnded(stepScope);
        BruteForceSolverPhaseScope bruteForceSolverPhaseScope = stepScope.getPhaseScope();
        // TODO The steps are too fine, so debug log is too much. Yet we still want some debug indication
        if (logger.isDebugEnabled()) {
            long timeMillisSpend = bruteForceSolverPhaseScope.calculateSolverTimeMillisSpend();
            logger.debug("    Step index ({}), time spend ({}), score ({}), {} best score ({}).",
                    stepScope.getStepIndex(), timeMillisSpend,
                    stepScope.getScore(), (stepScope.getBestScoreImproved() ? "new" : "   "),
                    bruteForceSolverPhaseScope.getBestScore());
        }
    }

    public void phaseEnded(BruteForceSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        bruteForceEntityWalker.phaseEnded(phaseScope);
        logger.info("Phase ({}) bruteForce ended: step total ({}), time spend ({}), best score ({}).",
                phaseIndex,
                phaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                phaseScope.calculateSolverTimeMillisSpend(),
                phaseScope.getBestScore());
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingEnded(solverScope);
        // TODO hook the walker up in the solver lifecycle (this will probably be fixed by selector unification)
//        bruteForceEntityWalker.solvingEnded(solverScope);
    }

}
