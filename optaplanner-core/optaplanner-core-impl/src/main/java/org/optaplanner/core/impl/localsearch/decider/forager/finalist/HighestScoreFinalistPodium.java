package org.optaplanner.core.impl.localsearch.decider.forager.finalist;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Default implementation of {@link FinalistPodium}.
 *
 * @see FinalistPodium
 */
public final class HighestScoreFinalistPodium<Solution_> extends AbstractFinalistPodium<Solution_> {

    protected Score finalistScore;

    @Override
    public void stepStarted(LocalSearchStepScope<Solution_> stepScope) {
        super.stepStarted(stepScope);
        finalistScore = null;
    }

    @Override
    public void addMove(LocalSearchMoveScope<Solution_> moveScope) {
        boolean accepted = moveScope.getAccepted();
        if (finalistIsAccepted && !accepted) {
            return;
        }
        if (accepted && !finalistIsAccepted) {
            finalistIsAccepted = true;
            finalistScore = null;
        }
        Score moveScore = moveScope.getScore();
        int scoreComparison = doComparison(moveScore);
        if (scoreComparison > 0) {
            finalistScore = moveScore;
            clearAndAddFinalist(moveScope);
        } else if (scoreComparison == 0) {
            addFinalist(moveScope);
        }
    }

    private int doComparison(Score moveScore) {
        if (finalistScore == null) {
            return 1;
        }
        return moveScore.compareTo(finalistScore);
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        finalistScore = null;
    }

}
