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

package org.drools.planner.core.constructionheuristic.greedyFit;

import java.util.Iterator;

import org.drools.planner.core.constructionheuristic.greedyFit.decider.GreedyDecider;
import org.drools.planner.core.constructionheuristic.greedyFit.selector.GreedyPlanningEntitySelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.solver.DefaultSolverScope;

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
        GreedyFitSolverPhaseScope solverPhaseScope = new GreedyFitSolverPhaseScope(solverScope);
        phaseStarted(solverPhaseScope);

        GreedyFitStepScope stepScope = createNextStepScope(solverPhaseScope, null);
        Iterator it = greedyPlanningEntitySelector.iterator();
        while (!termination.isPhaseTerminated(solverPhaseScope) && it.hasNext()) {
            Object planningEntity = it.next();
            stepScope.setPlanningEntity(planningEntity);
            stepStarted(stepScope);
            greedyDecider.decideNextStep(stepScope);
            Move nextStep = stepScope.getStep();
            if (nextStep == null) {
                logger.warn("    Cancelled step index ({}), time spend ({}): there is no doable move. Terminating phase early.",
                        stepScope.getStepIndex(),
                        solverPhaseScope.calculateSolverTimeMillisSpend());
                break;
            }
            nextStep.doMove(stepScope.getScoreDirector());
            // there is no need to recalculate the score, but we still need to set it
            solverPhaseScope.getWorkingSolution().setScore(stepScope.getScore());
            if (assertStepScoreIsUncorrupted) {
                solverPhaseScope.assertWorkingScore(stepScope.getScore());
            }
            if (!it.hasNext()) {
                stepScope.setSolutionInitialized(true);
            }
            stepEnded(stepScope);
            stepScope = createNextStepScope(solverPhaseScope, stepScope);
        }
        phaseEnded(solverPhaseScope);
    }

    private GreedyFitStepScope createNextStepScope(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope, GreedyFitStepScope completedGreedyFitStepScope) {
        if (completedGreedyFitStepScope == null) {
            completedGreedyFitStepScope = new GreedyFitStepScope(greedyFitSolverPhaseScope);
            completedGreedyFitStepScope.setScore(greedyFitSolverPhaseScope.getStartingScore());
            completedGreedyFitStepScope.setStepIndex(-1);
        }
        greedyFitSolverPhaseScope.setLastCompletedGreedyFitStepScope(completedGreedyFitStepScope);
        GreedyFitStepScope greedyFitStepScope = new GreedyFitStepScope(greedyFitSolverPhaseScope);
        greedyFitStepScope.setStepIndex(completedGreedyFitStepScope.getStepIndex() + 1);
        greedyFitStepScope.setSolutionInitialized(false);
        return greedyFitStepScope;
    }

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        // TODO hook the walker up in the solver lifecycle (this will probably be fixed by selector unification)
//        greedyPlanningEntitySelector.solvingStarted(solverScope);
//        greedyDecider.solvingStarted(solverScope);
    }

    public void phaseStarted(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        super.phaseStarted(greedyFitSolverPhaseScope);
        greedyPlanningEntitySelector.phaseStarted(greedyFitSolverPhaseScope);
        greedyDecider.phaseStarted(greedyFitSolverPhaseScope);
    }

    public void stepStarted(GreedyFitStepScope greedyFitStepScope) {
        super.stepStarted(greedyFitStepScope);
        greedyPlanningEntitySelector.stepStarted(greedyFitStepScope);
        greedyDecider.stepStarted(greedyFitStepScope);
    }

    public void stepEnded(GreedyFitStepScope greedyFitStepScope) {
        super.stepEnded(greedyFitStepScope);
        greedyPlanningEntitySelector.stepEnded(greedyFitStepScope);
        greedyDecider.stepEnded(greedyFitStepScope);
        if (logger.isDebugEnabled()) {
            long timeMillisSpend = greedyFitStepScope.getGreedyFitSolverPhaseScope().calculateSolverTimeMillisSpend();
            logger.debug("    Step index ({}), time spend ({}), score ({}), initialized planning entity ({}).",
                    new Object[]{greedyFitStepScope.getStepIndex(), timeMillisSpend,
                            greedyFitStepScope.getScore(), greedyFitStepScope.getPlanningEntity()});
        }
    }

    public void phaseEnded(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        super.phaseEnded(greedyFitSolverPhaseScope);
        greedyPlanningEntitySelector.phaseEnded(greedyFitSolverPhaseScope);
        greedyDecider.phaseEnded(greedyFitSolverPhaseScope);
        logger.info("Phase constructionHeuristic ended: step total ({}), time spend ({}), best score ({}).",
                new Object[]{greedyFitSolverPhaseScope.getLastCompletedStepScope().getStepIndex() + 1,
                greedyFitSolverPhaseScope.calculateSolverTimeMillisSpend(),
                greedyFitSolverPhaseScope.getBestScore()});
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        // TODO hook the walker up in the solver lifecycle (this will probably be fixed by selector unification)
//        greedyPlanningEntitySelector.solvingEnded(solverScope);
//        greedyDecider.solvingEnded(solverScope);
    }

}
