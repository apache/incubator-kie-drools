package org.optaplanner.core.impl.localsearch.decider.acceptor;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

import static org.mockito.Mockito.*;

public abstract class AbstractAcceptorTest {

    protected LocalSearchMoveScope buildMoveScope(LocalSearchStepScope stepScope, int score) {
        LocalSearchMoveScope moveScope = new LocalSearchMoveScope(stepScope);
        Move move = mock(Move.class);
        moveScope.setMove(move);
        moveScope.setScore(SimpleScore.valueOf(score));
        return moveScope;
    }

}
