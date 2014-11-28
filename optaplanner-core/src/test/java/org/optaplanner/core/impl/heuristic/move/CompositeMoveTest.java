/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class CompositeMoveTest {

    @Test
    public void createUndoMove() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        Move a = new DummyMove("a");
        Move b = new DummyMove("b");
        Move c = new DummyMove("c");
        CompositeMove move = new CompositeMove(a, b, c);
        CompositeMove undoMove = move.createUndoMove(scoreDirector);
        assertAllCodesOfArray(move.getMoves(), "a", "b", "c");
        assertAllCodesOfArray(undoMove.getMoves(), "undo c", "undo b", "undo a");
    }

    @Test
    public void doMove() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        Move a = mock(Move.class);
        Move b = mock(Move.class);
        Move c = mock(Move.class);
        CompositeMove move = new CompositeMove(a, b, c);
        move.doMove(scoreDirector);
        verify(a, times(1)).doMove(scoreDirector);
        verify(b, times(1)).doMove(scoreDirector);
        verify(c, times(1)).doMove(scoreDirector);
    }

    @Test
    public void buildEmptyMove() {
        Move move = CompositeMove.buildMove(new ArrayList<Move>());
        assertTrue(move instanceof NoChangeMove);

        move = CompositeMove.buildMove();
        assertTrue(move instanceof NoChangeMove);
    }

    @Test
    public void buildOneElemMove() {
        Move tmpMove = new DummyMove();
        Move move = CompositeMove.buildMove(Collections.singletonList(tmpMove));
        assertTrue(move instanceof DummyMove);

        move = CompositeMove.buildMove(tmpMove);
        assertTrue(move instanceof DummyMove);
    }

    @Test
    public void buildTwoElemMove() {
        Move first = new DummyMove();
        Move second = new NoChangeMove();
        Move move = CompositeMove.buildMove(Arrays.asList(first, second));
        assertTrue(move instanceof CompositeMove);
        assertTrue(((CompositeMove) move).getMoves()[0] instanceof DummyMove);
        assertTrue(((CompositeMove) move).getMoves()[1] instanceof NoChangeMove);

        move = CompositeMove.buildMove(first, second);
        assertTrue(move instanceof CompositeMove);
        assertTrue(((CompositeMove) move).getMoves()[0] instanceof DummyMove);
        assertTrue(((CompositeMove) move).getMoves()[1] instanceof NoChangeMove);
    }

    @Test
    public void isMoveDoable() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        Move first = new DummyMove();
        Move second = mock(DummyMove.class);
        when(second.isMoveDoable(scoreDirector)).thenReturn(false);
        Move move = CompositeMove.buildMove(first, second);
        assertFalse(move.isMoveDoable(scoreDirector));
    }

    @Test
    public void equals() {
        Move first = new DummyMove();
        Move second = new NoChangeMove();
        Move move = CompositeMove.buildMove(Arrays.asList(first, second));
        Move other = CompositeMove.buildMove(Arrays.asList(first, second));
        assertTrue(move.equals(other));

        move = CompositeMove.buildMove(Arrays.asList(first, second));
        other = CompositeMove.buildMove(Arrays.asList(second, first));
        assertFalse(move.equals(other));
        assertFalse(move.equals(new DummyMove()));
        assertTrue(move.equals(move));
    }
}
