package org.optaplanner.core.impl.localsearch.decider.acceptor.stepcountinghillclimbing;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class StepCountingHillClimbingAcceptor<Solution_> extends AbstractAcceptor<Solution_> {

    protected int stepCountingHillClimbingSize = -1;
    protected StepCountingHillClimbingType stepCountingHillClimbingType;

    protected Score thresholdScore;
    protected int count = -1;

    public StepCountingHillClimbingAcceptor(int stepCountingHillClimbingSize,
            StepCountingHillClimbingType stepCountingHillClimbingType) {
        this.stepCountingHillClimbingSize = stepCountingHillClimbingSize;
        this.stepCountingHillClimbingType = stepCountingHillClimbingType;
        if (stepCountingHillClimbingSize <= 0) {
            throw new IllegalArgumentException("The stepCountingHillClimbingSize (" + stepCountingHillClimbingSize
                    + ") cannot be negative or zero.");
        }
        if (stepCountingHillClimbingType == null) {
            throw new IllegalArgumentException("The stepCountingHillClimbingType (" + stepCountingHillClimbingType
                    + ") cannot be null.");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        thresholdScore = phaseScope.getBestScore();
        count = 0;
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope<Solution_> moveScope) {
        Score lastStepScore = moveScope.getStepScope().getPhaseScope().getLastCompletedStepScope().getScore();
        Score moveScore = moveScope.getScore();
        if (moveScore.compareTo(lastStepScore) >= 0) {
            return true;
        }
        return moveScore.compareTo(thresholdScore) >= 0;
    }

    @Override
    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        count += determineCountIncrement(stepScope);
        if (count >= stepCountingHillClimbingSize) {
            thresholdScore = stepScope.getScore();
            count = 0;
        }
    }

    private int determineCountIncrement(LocalSearchStepScope<Solution_> stepScope) {
        switch (stepCountingHillClimbingType) {
            case SELECTED_MOVE:
                long selectedMoveCount = stepScope.getSelectedMoveCount();
                return selectedMoveCount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) selectedMoveCount;
            case ACCEPTED_MOVE:
                long acceptedMoveCount = stepScope.getAcceptedMoveCount();
                return acceptedMoveCount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) acceptedMoveCount;
            case STEP:
                return 1;
            case EQUAL_OR_IMPROVING_STEP:
                return ((Score) stepScope.getScore()).compareTo(
                        stepScope.getPhaseScope().getLastCompletedStepScope().getScore()) >= 0 ? 1 : 0;
            case IMPROVING_STEP:
                return ((Score) stepScope.getScore()).compareTo(
                        stepScope.getPhaseScope().getLastCompletedStepScope().getScore()) > 0 ? 1 : 0;
            default:
                throw new IllegalStateException("The stepCountingHillClimbingType (" + stepCountingHillClimbingType
                        + ") is not implemented.");
        }
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        thresholdScore = null;
        count = -1;
    }

}
