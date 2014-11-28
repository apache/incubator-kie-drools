package org.optaplanner.core.impl.heuristic.move;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NoChangeMoveTest {

    private Move move;

    @Before
    public void init() {
        this.move = new NoChangeMove();
    }

    @Test
    public void isMoveDoable() {
        assertTrue(move.isMoveDoable(null));
    }

    @Test
    public void createUndoMove() {
        assertTrue(move.createUndoMove(null) instanceof  NoChangeMove);
    }

    @Test
    public void getPlanningEntities() {
        assertTrue(move.getPlanningEntities().isEmpty());
    }

    @Test
    public void getPlanningValues() {
        assertTrue(move.getPlanningValues().isEmpty());
    }
}
