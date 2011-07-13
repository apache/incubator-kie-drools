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
        GreedyFitSolverPhaseScope greedyFitSolverPhaseScope = new GreedyFitSolverPhaseScope(solverScope);
        phaseStarted(greedyFitSolverPhaseScope);

        GreedyFitStepScope greedyFitStepScope = createNextStepScope(greedyFitSolverPhaseScope, null);
        Iterator it = greedyPlanningEntitySelector.iterator();
        while (!termination.isPhaseTerminated(greedyFitSolverPhaseScope) && it.hasNext()) {
            Object planningEntity = it.next();
            greedyFitStepScope.setPlanningEntity(planningEntity);
            beforeDeciding(greedyFitStepScope);
            greedyDecider.decideNextStep(greedyFitStepScope);
            stepDecided(greedyFitStepScope);
            greedyFitStepScope.doStep();
            if (!it.hasNext()) {
                greedyFitStepScope.setSolutionInitialized(true);
            }
            if (assertStepScoreIsUncorrupted) {
                greedyFitSolverPhaseScope.assertWorkingScore(greedyFitStepScope.getScore());
            }
            stepTaken(greedyFitStepScope);
            greedyFitStepScope = createNextStepScope(greedyFitSolverPhaseScope, greedyFitStepScope);
        }
        phaseEnded(greedyFitSolverPhaseScope);
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

    public void beforeDeciding(GreedyFitStepScope greedyFitStepScope) {
        super.beforeDeciding(greedyFitStepScope);
        greedyPlanningEntitySelector.beforeDeciding(greedyFitStepScope);
    }

    public void stepDecided(GreedyFitStepScope greedyFitStepScope) {
        super.stepDecided(greedyFitStepScope);
        greedyPlanningEntitySelector.stepDecided(greedyFitStepScope);
    }

    public void stepTaken(GreedyFitStepScope greedyFitStepScope) {
        super.stepTaken(greedyFitStepScope);
        greedyPlanningEntitySelector.stepTaken(greedyFitStepScope);
        logger.info("Step index ({}), time spend ({}), score ({}), initialized planning entity ({}).",
                new Object[]{greedyFitStepScope.getStepIndex(),
                        greedyFitStepScope.getGreedyFitSolverPhaseScope().calculateSolverTimeMillisSpend(),
                        greedyFitStepScope.getScore(),
                        greedyFitStepScope.getPlanningEntity()});
    }

    public void phaseStarted(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        super.phaseStarted(greedyFitSolverPhaseScope);
        greedyPlanningEntitySelector.phaseStarted(greedyFitSolverPhaseScope);
    }

    public void phaseEnded(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        super.phaseEnded(greedyFitSolverPhaseScope);
        greedyPlanningEntitySelector.phaseEnded(greedyFitSolverPhaseScope);
        logger.info("Greedy phase ended at step index ({}) for best score ({}).",
                greedyFitSolverPhaseScope.getLastCompletedStepScope().getStepIndex(),
                greedyFitSolverPhaseScope.getBestScore());
    }

}
