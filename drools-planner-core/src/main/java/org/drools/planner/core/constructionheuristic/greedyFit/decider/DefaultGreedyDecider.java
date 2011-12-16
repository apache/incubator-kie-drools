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

package org.drools.planner.core.constructionheuristic.greedyFit.decider;

import org.drools.planner.core.heuristic.selector.variable.PlanningVariableWalker;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitSolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitStepScope;
import org.drools.planner.core.score.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGreedyDecider implements GreedyDecider {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private PlanningVariableWalker planningVariableWalker;
    private ConstructionHeuristicPickEarlyType constructionHeuristicPickEarlyType;

    protected boolean assertMoveScoreIsUncorrupted = false;

    public void setPlanningVariableWalker(PlanningVariableWalker planningVariableWalker) {
        this.planningVariableWalker = planningVariableWalker;
    }

    public void setConstructionHeuristicPickEarlyType(
            ConstructionHeuristicPickEarlyType constructionHeuristicPickEarlyType) {
        this.constructionHeuristicPickEarlyType = constructionHeuristicPickEarlyType;
    }

    public void setAssertMoveScoreIsUncorrupted(boolean assertMoveScoreIsUncorrupted) {
        this.assertMoveScoreIsUncorrupted = assertMoveScoreIsUncorrupted;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void phaseStarted(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        planningVariableWalker.phaseStarted(greedyFitSolverPhaseScope);
    }

    public void beforeDeciding(GreedyFitStepScope greedyFitStepScope) {
        planningVariableWalker.beforeDeciding(greedyFitStepScope);
    }

    public void decideNextStep(GreedyFitStepScope greedyFitStepScope) {
        GreedyFitSolverPhaseScope greedyFitSolverPhaseScope = greedyFitStepScope.getGreedyFitSolverPhaseScope();
        planningVariableWalker.initWalk(greedyFitStepScope.getPlanningEntity());
        Score lastStepScore = greedyFitSolverPhaseScope.getLastCompletedStepScope().getScore();
        Score maxScore = greedyFitSolverPhaseScope.getScoreDefinition().getPerfectMinimumScore();
        while (planningVariableWalker.hasWalk()) {
            planningVariableWalker.walk();
            Score score = greedyFitSolverPhaseScope.calculateScoreFromWorkingMemory();
            if (assertMoveScoreIsUncorrupted) {
                greedyFitSolverPhaseScope.assertWorkingScore(score);
            }
            if (score.compareTo(maxScore) > 0) {
                greedyFitStepScope.setVariableToValueMap(planningVariableWalker.getVariableToValueMap());
                maxScore = score;
            }
            // TODO refactor to usage of Move and MoveScope
            logger.trace("        Move score ({}) for planning entity ({}) for move (TODO).",
                    new Object[]{score, greedyFitStepScope.getPlanningEntity()});
            if (constructionHeuristicPickEarlyType
                    == ConstructionHeuristicPickEarlyType.FIRST_LAST_STEP_SCORE_EQUAL_OR_IMPROVING
                    && lastStepScore != null
                    && score.compareTo(lastStepScore) >= 0) {
                break;
            }
        }
        greedyFitStepScope.setScore(maxScore);
    }

    public void stepDecided(GreedyFitStepScope greedyFitStepScope) {
        planningVariableWalker.stepDecided(greedyFitStepScope);
    }

    public void stepTaken(GreedyFitStepScope greedyFitStepScope) {
        planningVariableWalker.stepTaken(greedyFitStepScope);
    }

    public void phaseEnded(GreedyFitSolverPhaseScope greedyFitSolverPhaseScope) {
        planningVariableWalker.phaseEnded(greedyFitSolverPhaseScope);
    }

}
