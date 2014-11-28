package org.optaplanner.core.impl.heuristic.move;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertInstanceOf;

public class NoChangeMoveTest {

    @Test
    public void isMoveDoable() {
        assertEquals(true, new NoChangeMove().isMoveDoable(null));
    }

    @Test
    public void createUndoMove() {
        assertInstanceOf(NoChangeMove.class, new NoChangeMove().createUndoMove(null));
    }

    @Test
    public void getPlanningEntities() {
        assertEquals(true, new NoChangeMove().getPlanningEntities().isEmpty());
    }

    @Test
    public void getPlanningValues() {
        assertEquals(true, new NoChangeMove().getPlanningValues().isEmpty());
    }

}
