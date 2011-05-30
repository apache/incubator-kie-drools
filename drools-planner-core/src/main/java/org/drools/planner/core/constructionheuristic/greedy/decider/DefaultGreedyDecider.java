package org.drools.planner.core.constructionheuristic.greedy.decider;

import org.drools.planner.core.bruteforce.BruteForcePlanningEntityIterator;
import org.drools.planner.core.constructionheuristic.greedy.GreedySolverScope;
import org.drools.planner.core.constructionheuristic.greedy.GreedyStepScope;
import org.drools.planner.core.score.Score;

public class DefaultGreedyDecider implements GreedyDecider {

    private PickEarlyFitType pickEarlyFitType;

    public void setPickEarlyFitType(PickEarlyFitType pickEarlyFitType) {
        this.pickEarlyFitType = pickEarlyFitType;
    }

    public void decideNextStep(GreedyStepScope greedyStepScope) {
        GreedySolverScope greedySolverScope = greedyStepScope.getGreedySolverScope();
        // TODO use greedySolverScope.getLastCompletedStepScope()
        Score lastStepScore = greedySolverScope.calculateScoreFromWorkingMemory();
        BruteForcePlanningEntityIterator bruteForcePlanningEntityIterator = new BruteForcePlanningEntityIterator(
                greedySolverScope, greedyStepScope.getPlanningEntity());
        Score maxScore = greedySolverScope.getScoreDefinition().getPerfectMinimumScore();
        greedySolverScope.getWorkingMemory().insert(greedyStepScope.getPlanningEntity()); // TODO let the BruteForcePlanningEntityIterator do that
        while (bruteForcePlanningEntityIterator.hasNext()) {
            bruteForcePlanningEntityIterator.next();
            Score score = greedySolverScope.calculateScoreFromWorkingMemory();
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
