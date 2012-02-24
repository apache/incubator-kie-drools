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

package org.drools.planner.core.localsearch;

import org.drools.planner.core.localsearch.decider.Decider;
import org.drools.planner.core.localsearch.event.LocalSearchSolverPhaseLifecycleListener;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * Default implementation of {@link LocalSearchSolverPhase}.
 */
public class DefaultLocalSearchSolverPhase extends AbstractSolverPhase implements LocalSearchSolverPhase,
        LocalSearchSolverPhaseLifecycleListener {

    protected Decider decider;

    protected boolean assertStepScoreIsUncorrupted = false;

    public void setDecider(Decider decider) {
        this.decider = decider;
        this.decider.setLocalSearchSolverPhase(this);
    }

    public void setAssertStepScoreIsUncorrupted(boolean assertStepScoreIsUncorrupted) {
        this.assertStepScoreIsUncorrupted = assertStepScoreIsUncorrupted;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        LocalSearchSolverPhaseScope solverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        phaseStarted(solverPhaseScope);

        LocalSearchStepScope stepScope = createNextStepScope(solverPhaseScope, null);
        while (!termination.isPhaseTerminated(solverPhaseScope)) {
            stepScope.setTimeGradient(termination.calculatePhaseTimeGradient(solverPhaseScope));
            beforeDeciding(stepScope);
            decider.decideNextStep(stepScope);
            Move nextStep = stepScope.getStep();
            if (nextStep == null) {
                logger.warn("    Cancelled step index ({}), time spend ({}): there is no doable move. Terminating phase early.",
                        stepScope.getStepIndex(),
                        solverPhaseScope.calculateSolverTimeMillisSpend());
                break;
            }
            stepDecided(stepScope);
            nextStep.doMove(stepScope.getWorkingMemory());
            // there is no need to recalculate the score, but we still need to set it
            solverPhaseScope.getWorkingSolution().setScore(stepScope.getScore());
            if (assertStepScoreIsUncorrupted) {
                solverPhaseScope.assertWorkingScore(stepScope.getScore());
            }
            stepTaken(stepScope);
            stepScope = createNextStepScope(solverPhaseScope, stepScope);
        }
        phaseEnded(solverPhaseScope);
    }

    private LocalSearchStepScope createNextStepScope(LocalSearchSolverPhaseScope localSearchSolverPhaseScope, LocalSearchStepScope completedLocalSearchStepScope) {
        if (completedLocalSearchStepScope == null) {
            completedLocalSearchStepScope = new LocalSearchStepScope(localSearchSolverPhaseScope);
            completedLocalSearchStepScope.setScore(localSearchSolverPhaseScope.getStartingScore());
            completedLocalSearchStepScope.setStepIndex(-1);
            completedLocalSearchStepScope.setTimeGradient(0.0);
        }
        localSearchSolverPhaseScope.setLastCompletedLocalSearchStepScope(completedLocalSearchStepScope);
        LocalSearchStepScope localSearchStepScope = new LocalSearchStepScope(localSearchSolverPhaseScope);
        localSearchStepScope.setStepIndex(completedLocalSearchStepScope.getStepIndex() + 1);
        localSearchStepScope.setSolutionInitialized(true);
        return localSearchStepScope;
    }

    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseStarted(localSearchSolverPhaseScope);
        decider.phaseStarted(localSearchSolverPhaseScope);
        if (!localSearchSolverPhaseScope.isWorkingSolutionInitialized()) {
            throw new IllegalStateException("Phase localSearch started with an uninitialized Solution." +
                    " First initialize the Solution. For example, run a phase constructionHeuristic first.");
        }
    }

    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        super.beforeDeciding(localSearchStepScope);
        decider.beforeDeciding(localSearchStepScope);
    }

    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        super.stepDecided(localSearchStepScope);
        decider.stepDecided(localSearchStepScope);
    }

    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        super.stepTaken(localSearchStepScope);
        decider.stepTaken(localSearchStepScope);
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = localSearchStepScope.getLocalSearchSolverPhaseScope();
        logger.debug("    Step index ({}), time spend ({}), score ({}), {} best score ({}), accepted move size ({})" +
                " for picked step ({}).",
                new Object[]{localSearchStepScope.getStepIndex(),
                        localSearchSolverPhaseScope.calculateSolverTimeMillisSpend(),
                        localSearchStepScope.getScore(),
                        (localSearchStepScope.getBestScoreImproved() ? "new" : "   "),
                        localSearchSolverPhaseScope.getBestScore(),
                        decider.getForager().getAcceptedMovesSize(),
                        localSearchStepScope.getStepString()});
    }

    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseEnded(localSearchSolverPhaseScope);
        decider.phaseEnded(localSearchSolverPhaseScope);
        logger.info("Phase localSearch ended: step total ({}), time spend ({}), best score ({}).",
                new Object[]{localSearchSolverPhaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                localSearchSolverPhaseScope.calculateSolverTimeMillisSpend(),
                localSearchSolverPhaseScope.getBestScore()});
    }

}
