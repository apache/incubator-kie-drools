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

import java.util.Arrays;

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
        CompositeMove move = new CompositeMove(Arrays.asList(a, b, c));
        CompositeMove undoMove = move.createUndoMove(scoreDirector);
        assertAllCodesOfIterator(move.getMoveList().iterator(), "a", "b", "c");
        assertAllCodesOfIterator(undoMove.getMoveList().iterator(), "undo c", "undo b", "undo a");
    }

    @Test
    public void doMove() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        Move a = mock(Move.class);
        Move b = mock(Move.class);
        Move c = mock(Move.class);
        CompositeMove move = new CompositeMove(Arrays.asList(a, b, c));
        move.doMove(scoreDirector);
        verify(a, times(1)).doMove(scoreDirector);
        verify(b, times(1)).doMove(scoreDirector);
        verify(c, times(1)).doMove(scoreDirector);
    }

}
