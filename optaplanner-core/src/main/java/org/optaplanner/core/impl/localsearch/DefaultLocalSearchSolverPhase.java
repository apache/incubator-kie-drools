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

package org.optaplanner.core.impl.localsearch;

import org.optaplanner.core.impl.localsearch.decider.Decider;
import org.optaplanner.core.impl.localsearch.event.LocalSearchSolverPhaseLifecycleListener;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Default implementation of {@link LocalSearchSolverPhase}.
 */
public class DefaultLocalSearchSolverPhase extends AbstractSolverPhase implements LocalSearchSolverPhase,
        LocalSearchSolverPhaseLifecycleListener {

    protected Decider decider;

    protected boolean assertStepScoreFromScratch = false;
    protected boolean assertExpectedStepScore = false;
    
    public Decider getDecider() {
        return this.decider;
    }

    public void setDecider(Decider decider) {
        this.decider = decider;
    }

    public void setAssertStepScoreFromScratch(boolean assertStepScoreFromScratch) {
        this.assertStepScoreFromScratch = assertStepScoreFromScratch;
    }

    public void setAssertExpectedStepScore(boolean assertExpectedStepScore) {
        this.assertExpectedStepScore = assertExpectedStepScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(solverScope);
        phaseStarted(phaseScope);

        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        while (!termination.isPhaseTerminated(phaseScope)) {
            stepScope.setTimeGradient(termination.calculatePhaseTimeGradient(phaseScope));
            stepStarted(stepScope);
            decider.decideNextStep(stepScope);
            Move nextStep = stepScope.getStep();
            if (nextStep == null) {
                if (termination.isPhaseTerminated(phaseScope)) {
                    logger.trace("    Step index ({}), time spend ({}) terminated without picking a nextStep.",
                            stepScope.getStepIndex(),
                            stepScope.getPhaseScope().calculateSolverTimeMillisSpend());
                } else if (stepScope.getSelectedMoveCount() == 0L) {
                    logger.warn("    No doable selected move at step index ({}), time spend ({})."
                            + " Terminating phase early.",
                            stepScope.getStepIndex(),
                            stepScope.getPhaseScope().calculateSolverTimeMillisSpend());
                } else {
                    throw new IllegalStateException("The step index (" + stepScope.getStepIndex()
                            + ") has accepted/selected move count (" + stepScope.getAcceptedMoveCount() + "/"
                            + stepScope.getSelectedMoveCount() + ") but failed to pick a nextStep (" + nextStep + ").");
                }
                // Although stepStarted has been called, stepEnded is not called for this step
                break;
            }
            nextStep.doMove(stepScope.getScoreDirector());
            // there is no need to recalculate the score, but we still need to set it
            phaseScope.getWorkingSolution().setScore(stepScope.getScore());
            if (assertStepScoreFromScratch) {
                phaseScope.assertWorkingScoreFromScratch(stepScope.getScore(), nextStep);
            }
            if (assertExpectedStepScore) {
                phaseScope.assertExpectedWorkingScore(stepScope.getScore(), nextStep);
            }
            stepEnded(stepScope);
            phaseScope.setLastCompletedStepScope(stepScope);
            stepScope = new LocalSearchStepScope(phaseScope);
        }
        phaseEnded(phaseScope);
    }

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        decider.solvingStarted(solverScope);
    }

    public void phaseStarted(LocalSearchSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        decider.phaseStarted(phaseScope);
        // TODO maybe this restriction should be lifted to allow LocalSearch to initialize a solution too?
        if (!phaseScope.getScoreDirector().isWorkingSolutionInitialized()) {
            throw new IllegalStateException("Phase localSearch started with an uninitialized Solution." +
                    " First initialize the Solution. For example, run a phase constructionHeuristic first.");
        }
    }

    public void stepStarted(LocalSearchStepScope stepScope) {
        super.stepStarted(stepScope);
        decider.stepStarted(stepScope);
    }

    public void stepEnded(LocalSearchStepScope stepScope) {
        super.stepEnded(stepScope);
        decider.stepEnded(stepScope);
        LocalSearchSolverPhaseScope phaseScope = stepScope.getPhaseScope();
        if (logger.isDebugEnabled()) {
            long timeMillisSpend = phaseScope.calculateSolverTimeMillisSpend();
            logger.debug("    Step index ({}), time spend ({}), score ({}), {} best score ({})," +
                    " accepted/selected move count ({}/{}) for picked step ({}).",
                    stepScope.getStepIndex(), timeMillisSpend,
                    stepScope.getScore(),
                    (stepScope.getBestScoreImproved() ? "new" : "   "),
                    phaseScope.getBestScore(),
                    stepScope.getAcceptedMoveCount(),
                    stepScope.getSelectedMoveCount(),
                    stepScope.getStepString());
        }
    }

    public void phaseEnded(LocalSearchSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        decider.phaseEnded(phaseScope);
        logger.info("Phase ({}) localSearch ended: step total ({}), time spend ({}), best score ({}).",
                phaseIndex,
                phaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                phaseScope.calculateSolverTimeMillisSpend(),
                phaseScope.getBestScore());
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingEnded(solverScope);
        decider.solvingEnded(solverScope);
    }

}
