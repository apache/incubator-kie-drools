package org.optaplanner.core.impl.constructionheuristic.decider.forager;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;

public class DefaultConstructionHeuristicForager<Solution_> extends AbstractConstructionHeuristicForager<Solution_> {

    protected final ConstructionHeuristicPickEarlyType pickEarlyType;

    protected long selectedMoveCount;
    protected ConstructionHeuristicMoveScope<Solution_> earlyPickedMoveScope;
    protected ConstructionHeuristicMoveScope<Solution_> maxScoreMoveScope;

    public DefaultConstructionHeuristicForager(ConstructionHeuristicPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void stepStarted(ConstructionHeuristicStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
        selectedMoveCount = 0L;
        earlyPickedMoveScope = null;
        maxScoreMoveScope = null;
    }

    @Override
    public void stepEnded(ConstructionHeuristicStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        earlyPickedMoveScope = null;
        maxScoreMoveScope = null;
    }

    @Override
    public void addMove(ConstructionHeuristicMoveScope<Solution_> moveScope) {
        selectedMoveCount++;
        checkPickEarly(moveScope);
        if (maxScoreMoveScope == null || moveScope.getScore().compareTo(maxScoreMoveScope.getScore()) > 0) {
            maxScoreMoveScope = moveScope;
        }
    }

    protected void checkPickEarly(ConstructionHeuristicMoveScope<Solution_> moveScope) {
        switch (pickEarlyType) {
            case NEVER:
                break;
            case FIRST_NON_DETERIORATING_SCORE:
                Score lastStepScore = moveScope.getStepScope().getPhaseScope()
                        .getLastCompletedStepScope().getScore();
                if (moveScope.getScore().withInitScore(0).compareTo(lastStepScore.withInitScore(0)) >= 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            case FIRST_FEASIBLE_SCORE:
                if (moveScope.getScore().withInitScore(0).isFeasible()) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            case FIRST_FEASIBLE_SCORE_OR_NON_DETERIORATING_HARD:
                Score lastStepScore2 = moveScope.getStepScope().getPhaseScope()
                        .getLastCompletedStepScope().getScore();
                Score lastStepScoreDifference = moveScope.getScore().withInitScore(0)
                        .subtract(lastStepScore2.withInitScore(0));
                if (lastStepScoreDifference.isFeasible()) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            default:
                throw new IllegalStateException("The pickEarlyType (" + pickEarlyType + ") is not implemented.");
        }
    }

    @Override
    public boolean isQuitEarly() {
        return earlyPickedMoveScope != null;
    }

    @Override
    public ConstructionHeuristicMoveScope<Solution_> pickMove(ConstructionHeuristicStepScope<Solution_> stepScope) {
        stepScope.setSelectedMoveCount(selectedMoveCount);
        if (earlyPickedMoveScope != null) {
            return earlyPickedMoveScope;
        } else {
            return maxScoreMoveScope;
        }
    }

}
