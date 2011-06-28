package org.drools.planner.core.constructionheuristic.greedy.decider;

import org.drools.planner.core.bruteforce.BruteForcePlanningEntityIterator;
import org.drools.planner.core.constructionheuristic.greedy.GreedySolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedy.GreedyStepScope;
import org.drools.planner.core.score.Score;

public class DefaultGreedyDecider implements GreedyDecider {

    private PickEarlyFitType pickEarlyFitType;

    public void setPickEarlyFitType(PickEarlyFitType pickEarlyFitType) {
        this.pickEarlyFitType = pickEarlyFitType;
    }

    public void decideNextStep(GreedyStepScope greedyStepScope) {
        GreedySolverPhaseScope greedySolverPhaseScope = greedyStepScope.getGreedySolverPhaseScope();
        // TODO use greedySolverPhaseScope.getLastCompletedStepScope()
        Score lastStepScore = greedySolverPhaseScope.calculateScoreFromWorkingMemory();
        BruteForcePlanningEntityIterator bruteForcePlanningEntityIterator = new BruteForcePlanningEntityIterator(
                greedySolverPhaseScope, greedyStepScope.getPlanningEntity());
        Score maxScore = greedySolverPhaseScope.getScoreDefinition().getPerfectMinimumScore();
        while (bruteForcePlanningEntityIterator.hasNext()) {
            bruteForcePlanningEntityIterator.next();
            Score score = greedySolverPhaseScope.calculateScoreFromWorkingMemory();
            if (score.compareTo(maxScore) > 0) {
                greedyStepScope.setVariableToValueMap(bruteForcePlanningEntityIterator.getVariableToValueMap());
                maxScore = score;
            }
            if (pickEarlyFitType == PickEarlyFitType.FIRST_LAST_STEP_SCORE_IMPROVING_OR_EQUAL
                    && score.compareTo(lastStepScore) >= 0) {
                break;
            }
        }
        greedyStepScope.setScore(maxScore);
    }

}
