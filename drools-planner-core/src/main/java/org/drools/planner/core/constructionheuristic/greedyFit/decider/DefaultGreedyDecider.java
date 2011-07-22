package org.drools.planner.core.constructionheuristic.greedyFit.decider;

import org.drools.planner.core.heuristic.selector.variable.PlanningVariableWalker;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitSolverPhaseScope;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitStepScope;
import org.drools.planner.core.score.Score;

public class DefaultGreedyDecider implements GreedyDecider {

    private PlanningVariableWalker planningVariableWalker;
    private PickEarlyGreedyFitType pickEarlyGreedyFitType;

    protected boolean assertMoveScoreIsUncorrupted = false;

    public void setPlanningVariableWalker(PlanningVariableWalker planningVariableWalker) {
        this.planningVariableWalker = planningVariableWalker;
    }

    public void setPickEarlyGreedyFitType(PickEarlyGreedyFitType pickEarlyGreedyFitType) {
        this.pickEarlyGreedyFitType = pickEarlyGreedyFitType;
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
            if (pickEarlyGreedyFitType == PickEarlyGreedyFitType.FIRST_LAST_STEP_SCORE_IMPROVING_OR_EQUAL
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
