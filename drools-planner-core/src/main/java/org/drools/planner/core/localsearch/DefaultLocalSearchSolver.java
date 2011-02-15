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
import org.drools.planner.core.localsearch.termination.Termination;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.solver.AbstractSolver;
import org.drools.planner.core.solver.AbstractSolverScope;

/**
 * Default implementation of {@link LocalSearchSolver}.
 * @author Geoffrey De Smet
 */
public class DefaultLocalSearchSolver extends AbstractSolver implements LocalSearchSolver,
        LocalSearchSolverLifecycleListener {

    protected Termination termination;
    protected Decider decider;

    protected boolean assertStepScoreIsUncorrupted = false;

    protected LocalSearchSolverScope localSearchSolverScope = new LocalSearchSolverScope();

    public void setTermination(Termination termination) {
        this.termination = termination;
        this.termination.setLocalSearchSolver(this);
    }

    public Decider getDecider() {
        return decider;
    }

    public void setDecider(Decider decider) {
        this.decider = decider;
        this.decider.setLocalSearchSolver(this);
    }

    public void setAssertStepScoreIsUncorrupted(boolean assertStepScoreIsUncorrupted) {
        this.assertStepScoreIsUncorrupted = assertStepScoreIsUncorrupted;
    }

    public AbstractSolverScope getAbstractSolverScope() {
        return localSearchSolverScope;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    protected void solveImplementation() {
        LocalSearchSolverScope localSearchSolverScope = this.localSearchSolverScope;
        solvingStarted(localSearchSolverScope);

        LocalSearchStepScope localSearchStepScope = createNextStepScope(localSearchSolverScope, null);
        while (!terminatedEarly.get() && !termination.isTerminated(localSearchStepScope)) {
            localSearchStepScope.setTimeGradient(termination.calculateTimeGradient(localSearchStepScope));
            beforeDeciding(localSearchStepScope);
            decider.decideNextStep(localSearchStepScope);
            Move nextStep = localSearchStepScope.getStep();
            if (nextStep == null) {
                // TODO JBRULES-2213 do not terminate, but warn and try again
                logger.warn("No move accepted for step index ({}) out of {} accepted moves. Terminating by exception.",
                        localSearchStepScope.getStepIndex(), decider.getForager().getAcceptedMovesSize());
                break;
            }
            String nextStepString = null;
            if (logger.isInfoEnabled()) {
                nextStepString = nextStep.toString();
            }
            stepDecided(localSearchStepScope);
            nextStep.doMove(localSearchStepScope.getWorkingMemory());
            // there is no need to recalculate the score, but we still need to set it
            localSearchSolverScope.getWorkingSolution().setScore(localSearchStepScope.getScore());
            if (assertStepScoreIsUncorrupted) {
                localSearchSolverScope.assertWorkingScore(localSearchStepScope.getScore());
            }
            stepTaken(localSearchStepScope);
            logger.info("Step index ({}), time spend ({}), score ({}), {} best score ({}), accepted move size ({}) for picked step ({}).",
                    new Object[]{localSearchStepScope.getStepIndex(), localSearchSolverScope.calculateTimeMillisSpend(),
                            localSearchStepScope.getScore(), (localSearchStepScope.getBestScoreImproved() ? "new" : "   "),
                            localSearchSolverScope.getBestScore(),
                            decider.getForager().getAcceptedMovesSize(), nextStepString});
            localSearchStepScope = createNextStepScope(localSearchSolverScope, localSearchStepScope);
        }
        solvingEnded(localSearchSolverScope);
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

    public void solvingStarted(LocalSearchSolverScope localSearchSolverScope) {
        super.solvingStarted(localSearchSolverScope);
        termination.solvingStarted(localSearchSolverScope);
        decider.solvingStarted(localSearchSolverScope);
    }

    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        termination.beforeDeciding(localSearchStepScope);
        decider.beforeDeciding(localSearchStepScope);
    }

    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        termination.stepDecided(localSearchStepScope);
        decider.stepDecided(localSearchStepScope);
    }

    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        bestSolutionRecaller.stepTaken(localSearchStepScope);
        termination.stepTaken(localSearchStepScope);
        decider.stepTaken(localSearchStepScope);
    }

    public void solvingEnded(LocalSearchSolverScope localSearchSolverScope) {
        decider.solvingEnded(localSearchSolverScope);
        termination.solvingEnded(localSearchSolverScope);
        bestSolutionRecaller.solvingEnded(localSearchSolverScope);
        long timeMillisSpend = localSearchSolverScope.calculateTimeMillisSpend();
        long averageCalculateCountPerSecond = localSearchSolverScope.getCalculateCount() * 1000L / timeMillisSpend;
        logger.info("Solved at step index ({}) with time spend ({}) for best score ({})"
                + " with average calculate count per second ({}).", new Object[]{
                localSearchSolverScope.getLastCompletedLocalSearchStepScope().getStepIndex(),
                timeMillisSpend,
                localSearchSolverScope.getBestScore(),
                averageCalculateCountPerSecond
        });
    }

}
