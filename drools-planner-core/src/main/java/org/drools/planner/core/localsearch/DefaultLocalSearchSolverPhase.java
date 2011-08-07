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
        LocalSearchSolverPhaseScope localSearchSolverPhaseScope = new LocalSearchSolverPhaseScope(solverScope);
        phaseStarted(localSearchSolverPhaseScope);

        LocalSearchStepScope localSearchStepScope = createNextStepScope(localSearchSolverPhaseScope, null);
        while (!termination.isPhaseTerminated(localSearchSolverPhaseScope)) {
            localSearchStepScope.setTimeGradient(termination.calculatePhaseTimeGradient(localSearchSolverPhaseScope));
            beforeDeciding(localSearchStepScope);
            decider.decideNextStep(localSearchStepScope);
            Move nextStep = localSearchStepScope.getStep();
            if (nextStep == null) {
                // TODO JBRULES-2213 do not terminate, but warn and try again
                logger.warn("No move accepted for step index ({}) out of {} accepted moves. Terminating by exception.",
                        localSearchStepScope.getStepIndex(), decider.getForager().getAcceptedMovesSize());
                break;
            }
            stepDecided(localSearchStepScope);
            nextStep.doMove(localSearchStepScope.getWorkingMemory());
            // there is no need to recalculate the score, but we still need to set it
            localSearchSolverPhaseScope.getWorkingSolution().setScore(localSearchStepScope.getScore());
            if (assertStepScoreIsUncorrupted) {
                localSearchSolverPhaseScope.assertWorkingScore(localSearchStepScope.getScore());
            }
            stepTaken(localSearchStepScope);
            localSearchStepScope = createNextStepScope(localSearchSolverPhaseScope, localSearchStepScope);
        }
        phaseEnded(localSearchSolverPhaseScope);
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
        logger.info("Step index ({}), time spend ({}), score ({}), {} best score ({}), accepted move size ({})" +
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
        AbstractStepScope lastCompletedStepScope = localSearchSolverPhaseScope.getLastCompletedStepScope();
        logger.info("Local search phase ended at step index ({}) for best score ({}).",
                lastCompletedStepScope.getStepIndex(),
                localSearchSolverPhaseScope.getBestScore());
    }

}
