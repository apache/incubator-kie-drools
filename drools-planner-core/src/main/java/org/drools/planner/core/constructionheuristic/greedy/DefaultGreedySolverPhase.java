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

package org.drools.planner.core.constructionheuristic.greedy;

import java.util.Iterator;

import org.drools.planner.core.constructionheuristic.greedy.decider.GreedyDecider;
import org.drools.planner.core.constructionheuristic.greedy.selector.GreedyPlanningEntitySelector;
import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * Default implementation of {@link GreedySolverPhase}.
 */
public class DefaultGreedySolverPhase extends AbstractSolverPhase implements GreedySolverPhase {

    protected GreedyPlanningEntitySelector greedyPlanningEntitySelector;
    protected GreedyDecider greedyDecider;
    
    public void setGreedyPlanningEntitySelector(GreedyPlanningEntitySelector greedyPlanningEntitySelector) {
        this.greedyPlanningEntitySelector = greedyPlanningEntitySelector;
    }

    public void setGreedyDecider(GreedyDecider greedyDecider) {
        this.greedyDecider = greedyDecider;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solve(DefaultSolverScope solverScope) {
        GreedySolverPhaseScope greedySolverPhaseScope = new GreedySolverPhaseScope(solverScope);
        phaseStarted(greedySolverPhaseScope);

        GreedyStepScope greedyStepScope = createNextStepScope(greedySolverPhaseScope, null);
        Iterator it = greedyPlanningEntitySelector.iterator();
        while (!mustTerminate(greedyStepScope) && it.hasNext()) {
            Object planningEntity = it.next();
            greedyStepScope.setPlanningEntity(planningEntity);
            beforeDeciding(greedyStepScope);
            greedyDecider.decideNextStep(greedyStepScope);
            stepDecided(greedyStepScope);
            greedyStepScope.doStep();
            if (!it.hasNext()) {
                greedyStepScope.setSolutionInitialized(true);
            }
            stepTaken(greedyStepScope);
            greedyStepScope = createNextStepScope(greedySolverPhaseScope, greedyStepScope);
        }
        phaseEnded(greedySolverPhaseScope);
    }

    private GreedyStepScope createNextStepScope(GreedySolverPhaseScope greedySolverPhaseScope, GreedyStepScope completedGreedyStepScope) {
        if (completedGreedyStepScope == null) {
            completedGreedyStepScope = new GreedyStepScope(greedySolverPhaseScope);
            completedGreedyStepScope.setScore(greedySolverPhaseScope.getStartingScore());
            completedGreedyStepScope.setStepIndex(-1);
        }
        greedySolverPhaseScope.setLastCompletedGreedyStepScope(completedGreedyStepScope);
        GreedyStepScope greedyStepScope = new GreedyStepScope(greedySolverPhaseScope);
        greedyStepScope.setStepIndex(completedGreedyStepScope.getStepIndex() + 1);
        greedyStepScope.setSolutionInitialized(false);
        return greedyStepScope;
    }

    public void beforeDeciding(GreedyStepScope greedyStepScope) {
        super.beforeDeciding(greedyStepScope);
        greedyPlanningEntitySelector.beforeDeciding(greedyStepScope);
    }

    public void stepDecided(GreedyStepScope greedyStepScope) {
        super.stepDecided(greedyStepScope);
        greedyPlanningEntitySelector.stepDecided(greedyStepScope);
    }

    public void stepTaken(GreedyStepScope greedyStepScope) {
        super.stepTaken(greedyStepScope);
        greedyPlanningEntitySelector.stepTaken(greedyStepScope);
        logger.info("Step index ({}), time spend ({}), score ({}), initialized planning entity ({}).",
                new Object[]{greedyStepScope.getStepIndex(),
                        greedyStepScope.getGreedySolverPhaseScope().calculateSolverTimeMillisSpend(),
                        greedyStepScope.getScore(),
                        greedyStepScope.getPlanningEntity()});
    }

    public void phaseStarted(GreedySolverPhaseScope greedySolverPhaseScope) {
        super.phaseStarted(greedySolverPhaseScope);
        greedyPlanningEntitySelector.phaseStarted(greedySolverPhaseScope);
    }

    public void phaseEnded(GreedySolverPhaseScope greedySolverPhaseScope) {
        super.phaseEnded(greedySolverPhaseScope);
        greedyPlanningEntitySelector.phaseEnded(greedySolverPhaseScope);
        logger.info("Greedy phase ended at step index ({}) for best score ({}).",
                greedySolverPhaseScope.getLastCompletedStepScope().getStepIndex(),
                greedySolverPhaseScope.getBestScore());
    }

}
