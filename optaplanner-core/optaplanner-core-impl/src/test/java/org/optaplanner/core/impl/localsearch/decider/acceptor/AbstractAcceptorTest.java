package org.optaplanner.core.impl.localsearch.decider.acceptor;

import static org.mockito.Mockito.mock;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public abstract class AbstractAcceptorTest {

    protected <Solution_> LocalSearchMoveScope<Solution_> buildMoveScope(
            LocalSearchStepScope<Solution_> stepScope, int score) {
        Move<Solution_> move = mock(Move.class);
        LocalSearchMoveScope<Solution_> moveScope = new LocalSearchMoveScope<>(stepScope, 0, move);
        moveScope.setScore(SimpleScore.of(score));
        return moveScope;
    }

}
