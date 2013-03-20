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

package org.optaplanner.core.impl.constructionheuristic.greedyFit;

import java.util.Iterator;

import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.GreedyDecider;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitSolverPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitStepScope;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.selector.GreedyPlanningEntitySelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Default implementation of {@link GreedyFitSolverPhase}.
 */
public class DefaultGreedyFitSolverPhase extends AbstractSolverPhase implements GreedyFitSolverPhase {

    protected GreedyPlanningEntitySelector greedyPlanningEntitySelector;
    protected GreedyDecider greedyDecider;

    protected boolean assertStepScoreIsUncorrupted = false;
    
    public void setGreedyPlanningEntitySelector(GreedyPlanningEntitySelector greedyPlanningEntitySelector) {
        this.greedyPlanningEntitySelector = greedyPlanningEntitySelector;
    }

    public void setGreedyDecider(GreedyDecider greedyDecider) {
        this.greedyDecider = greedyDecider;
    }

    public void setAssertStepScoreIsUncorrupted(boolean assertStepScoreIsUncorrupted) {
        this.assertStepScoreIsUncorrupted = assertStepScoreIsUncorrupted;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        GreedyFitSolverPhaseScope phaseScope = new GreedyFitSolverPhaseScope(solverScope);
        phaseStarted(phaseScope);

        GreedyFitStepScope stepScope = createNextStepScope(phaseScope, null);
        Iterator it = greedyPlanningEntitySelector.iterator();
        while (!termination.isPhaseTerminated(phaseScope) && it.hasNext()) {
            Object planningEntity = it.next();
            stepScope.setPlanningEntity(planningEntity);
            stepStarted(stepScope);
            greedyDecider.decideNextStep(stepScope);
            Move nextStep = stepScope.getStep();
            if (nextStep == null) {
                logger.warn("    Cancelled step index ({}), time spend ({}): there is no doable move. Terminating phase early.",
                        stepScope.getStepIndex(),
                        phaseScope.calculateSolverTimeMillisSpend());
                break;
            }
            nextStep.doMove(stepScope.getScoreDirector());
            // there is no need to recalculate the score, but we still need to set it
            phaseScope.getWorkingSolution().setScore(stepScope.getScore());
            if (assertStepScoreIsUncorrupted) {
                phaseScope.assertWorkingScoreFromScratch(stepScope.getScore());
                phaseScope.assertExpectedWorkingScore(stepScope.getScore());
            }
            stepEnded(stepScope);
            stepScope = createNextStepScope(phaseScope, stepScope);
        }
        phaseEnded(phaseScope);
    }

    private GreedyFitStepScope createNextStepScope(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope, GreedyFitStepScope completedGreedyFitStepScope) {
        if (completedGreedyFitStepScope == null) {
            completedGreedyFitStepScope = new GreedyFitStepScope(greedyFitSolverPhaseScope);
            completedGreedyFitStepScope.setScore(greedyFitSolverPhaseScope.getStartingScore());
            completedGreedyFitStepScope.setStepIndex(-1);
        }
        greedyFitSolverPhaseScope.setLastCompletedStepScope(completedGreedyFitStepScope);
        GreedyFitStepScope greedyFitStepScope = new GreedyFitStepScope(greedyFitSolverPhaseScope);
        greedyFitStepScope.setStepIndex(completedGreedyFitStepScope.getStepIndex() + 1);
        return greedyFitStepScope;
    }

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        // TODO hook the walker up in the solver lifecycle (this will probably be fixed by selector unification)
//        greedyPlanningEntitySelector.solvingStarted(solverScope);
//        greedyDecider.solvingStarted(solverScope);
    }

    public void phaseStarted(GreedyFitSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        greedyPlanningEntitySelector.phaseStarted(phaseScope);
        greedyDecider.phaseStarted(phaseScope);
    }

    public void stepStarted(GreedyFitStepScope stepScope) {
        super.stepStarted(stepScope);
        greedyPlanningEntitySelector.stepStarted(stepScope);
        greedyDecider.stepStarted(stepScope);
    }

    public void stepEnded(GreedyFitStepScope stepScope) {
        super.stepEnded(stepScope);
        greedyPlanningEntitySelector.stepEnded(stepScope);
        greedyDecider.stepEnded(stepScope);
        if (logger.isDebugEnabled()) {
            long timeMillisSpend = stepScope.getPhaseScope().calculateSolverTimeMillisSpend();
            logger.debug("    Step index ({}), time spend ({}), score ({}), initialized planning entity ({}).",
                    stepScope.getStepIndex(), timeMillisSpend,
                    stepScope.getScore(), stepScope.getPlanningEntity());
        }
    }

    public void phaseEnded(GreedyFitSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        Solution newBestSolution = phaseScope.getScoreDirector().cloneWorkingSolution();
        int newBestUninitializedVariableCount = phaseScope.getSolutionDescriptor()
                .countUninitializedVariables(newBestSolution);
        bestSolutionRecaller.updateBestSolution(phaseScope.getSolverScope(),
                newBestSolution, newBestUninitializedVariableCount);
        greedyPlanningEntitySelector.phaseEnded(phaseScope);
        greedyDecider.phaseEnded(phaseScope);
        logger.info("Phase ({}) constructionHeuristic ended: step total ({}), time spend ({}), best score ({}).",
                phaseIndex,
                phaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                phaseScope.calculateSolverTimeMillisSpend(),
                phaseScope.getBestScore());
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        // TODO hook the walker up in the solver lifecycle (this will probably be fixed by selector unification)
//        greedyPlanningEntitySelector.solvingEnded(solverScope);
//        greedyDecider.solvingEnded(solverScope);
    }

}
