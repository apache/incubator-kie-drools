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

import org.drools.planner.core.constructionheuristic.greedy.decider.GreedyDecider;
import org.drools.planner.core.constructionheuristic.greedy.selector.GreedyPlanningEntitySelector;
import org.drools.planner.core.domain.PlanningEntity;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solver.AbstractSolver;
import org.drools.planner.core.solver.AbstractSolverScope;

/**
 * Default implementation of {@link GreedySolver}.
 */
public class DefaultGreedySolver extends AbstractSolver implements GreedySolver {

    protected GreedyPlanningEntitySelector greedyPlanningEntitySelector;
    protected GreedyDecider greedyDecider;
    
    protected GreedySolverScope greedySolverScope = new GreedySolverScope();

    public void setGreedyPlanningEntitySelector(GreedyPlanningEntitySelector greedyPlanningEntitySelector) {
        this.greedyPlanningEntitySelector = greedyPlanningEntitySelector;
    }

    public void setGreedyDecider(GreedyDecider greedyDecider) {
        this.greedyDecider = greedyDecider;
    }

    @Override
    public AbstractSolverScope getAbstractSolverScope() {
        return greedySolverScope;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    protected void solveImplementation() {
        GreedySolverScope greedySolverScope = this.greedySolverScope;
        solvingStarted(greedySolverScope);


        GreedyStepScope greedyStepScope = createNextStepScope(greedySolverScope, null);
        for (Object planningEntity : greedyPlanningEntitySelector) {
            greedyStepScope.setPlanningEntity(planningEntity);
            greedyDecider.decideNextStep(greedyStepScope);
            greedyStepScope.doStep();
            logger.info("Step index ({}), time spend ({}), score ({}), initializing entities.",
                    new Object[]{greedyStepScope.getStepIndex(), greedySolverScope.calculateTimeMillisSpend(),
                            greedyStepScope.getScore()});
            greedyStepScope = createNextStepScope(greedySolverScope, greedyStepScope);
        }
        bestSolutionRecaller.stepTaken(greedyStepScope); // TODO rename stepTaken or use isSolutionInitialized()
        solvingEnded(greedySolverScope);
    }

    private GreedyStepScope createNextStepScope(GreedySolverScope greedySolverScope, GreedyStepScope completedGreedyStepScope) {
        if (completedGreedyStepScope == null) {
            completedGreedyStepScope = new GreedyStepScope(greedySolverScope);
            completedGreedyStepScope.setScore(greedySolverScope.getStartingScore());
            completedGreedyStepScope.setStepIndex(-1);
        }
//        greedySolverScope.setLastCompletedLocalSearchStepScope(completedGreedyStepScope); TODO add back in
        GreedyStepScope greedyStepScope = new GreedyStepScope(greedySolverScope);
        greedyStepScope.setStepIndex(completedGreedyStepScope.getStepIndex() + 1);
        return greedyStepScope;
    }

    public void solvingStarted(GreedySolverScope greedySolverScope) {
        super.solvingStarted(greedySolverScope);
        greedyPlanningEntitySelector.solvingStarted(greedySolverScope);
    }

    public void solvingEnded(GreedySolverScope greedySolverScope) {
        greedyPlanningEntitySelector.solvingEnded(greedySolverScope);
        bestSolutionRecaller.solvingEnded(greedySolverScope);
        long timeMillisSpend = greedySolverScope.calculateTimeMillisSpend();
        long averageCalculateCountPerSecond = greedySolverScope.getCalculateCount() * 1000L / timeMillisSpend;
        logger.info("Solved with time spend ({}) for best score ({})"
                + " with average calculate count per second ({}).", new Object[]{
                timeMillisSpend,
                greedySolverScope.getBestScore(),
                averageCalculateCountPerSecond
        });
    }

}
