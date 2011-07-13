package org.drools.planner.core.constructionheuristic.greedyFit.decider;

import org.drools.planner.core.bruteforce.BruteForcePlanningEntityIterator;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitSolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitStepScope;
import org.drools.planner.core.score.Score;

public class DefaultGreedyDecider implements GreedyDecider {

    private PickEarlyFitType pickEarlyFitType;

    protected boolean assertMoveScoreIsUncorrupted = false;

    public void setPickEarlyFitType(PickEarlyFitType pickEarlyFitType) {
        this.pickEarlyFitType = pickEarlyFitType;
    }

    public void setAssertMoveScoreIsUncorrupted(boolean assertMoveScoreIsUncorrupted) {
        this.assertMoveScoreIsUncorrupted = assertMoveScoreIsUncorrupted;
    }

    public void decideNextStep(GreedyFitStepScope greedyFitStepScope) {
        GreedyFitSolverPhaseScope greedyFitSolverPhaseScope = greedyFitStepScope.getGreedyFitSolverPhaseScope();
        Score lastStepScore = greedyFitSolverPhaseScope.getLastCompletedStepScope().getScore();
        BruteForcePlanningEntityIterator bruteForcePlanningEntityIterator = new BruteForcePlanningEntityIterator(
                greedyFitSolverPhaseScope, greedyFitStepScope.getPlanningEntity());
        Score maxScore = greedyFitSolverPhaseScope.getScoreDefinition().getPerfectMinimumScore();
        while (bruteForcePlanningEntityIterator.hasNext()) {
            bruteForcePlanningEntityIterator.next();
            Score score = greedyFitSolverPhaseScope.calculateScoreFromWorkingMemory();
            if (assertMoveScoreIsUncorrupted) {
                greedyFitSolverPhaseScope.assertWorkingScore(score);
            }
            if (score.compareTo(maxScore) > 0) {
                greedyFitStepScope.setVariableToValueMap(bruteForcePlanningEntityIterator.getVariableToValueMap());
                maxScore = score;
            }
            if (pickEarlyFitType == PickEarlyFitType.FIRST_LAST_STEP_SCORE_IMPROVING_OR_EQUAL
                    && score.compareTo(lastStepScore) >= 0) {
                break;
            }
        }
        greedyFitStepScope.setScore(maxScore);
    }

}
