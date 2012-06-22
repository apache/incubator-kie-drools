/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.heuristic.selector.move.cached;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.DummyMove;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.drools.planner.core.testdata.util.PlannerAssert.*;
import static org.mockito.Mockito.*;

public class ShufflingMoveSelectorTest {

    @Test
    public void cacheTypeSolver() {
        runCacheType(SelectionCacheType.SOLVER, 1);
    }

    @Test
    public void cacheTypePhase() {
        runCacheType(SelectionCacheType.PHASE, 2);
    }

    @Test
    public void cacheTypeStep() {
        runCacheType(SelectionCacheType.STEP, 3);
    }

    public void runCacheType(SelectionCacheType cacheType, int timesCalled) {
        MoveSelector childMoveSelector = mock(MoveSelector.class);
        final List<Move> moveList = Arrays.<Move>asList(new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3"));
        when(childMoveSelector.iterator()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return moveList.iterator();
            }
        });
        when(childMoveSelector.isContinuous()).thenReturn(false);
        when(childMoveSelector.isNeverEnding()).thenReturn(false);
        when(childMoveSelector.getSize()).thenReturn((long) moveList.size());

        ShufflingMoveSelector moveSelector = new ShufflingMoveSelector(childMoveSelector, cacheType);
        verify(childMoveSelector, times(1)).isNeverEnding();

        Random workingRandom = mock(Random.class);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeA.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA1.getWorkingRandom()).thenReturn(workingRandom);
        when(workingRandom.nextInt(3)).thenReturn(2);
        when(workingRandom.nextInt(2)).thenReturn(0);
        moveSelector.stepStarted(stepScopeA1);
        runAsserts(moveSelector, "a2", "a1", "a3");
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA2.getWorkingRandom()).thenReturn(workingRandom);
        when(workingRandom.nextInt(3)).thenReturn(0);
        when(workingRandom.nextInt(2)).thenReturn(1);
        moveSelector.stepStarted(stepScopeA2);
        if (cacheType.compareTo(SelectionCacheType.STEP) > 0) {
            // From a1, a2, a3
            runAsserts(moveSelector, "a3", "a1", "a2");
        } else {
            // Reset from a1, a2, a3
            runAsserts(moveSelector, "a3", "a2", "a1");
        }
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeB.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB1.getWorkingRandom()).thenReturn(workingRandom);
        when(workingRandom.nextInt(3)).thenReturn(1);
        when(workingRandom.nextInt(2)).thenReturn(0);
        moveSelector.stepStarted(stepScopeB1);
        if (cacheType.compareTo(SelectionCacheType.PHASE) > 0) {
            // From a3, a1, a2
            runAsserts(moveSelector, "a2", "a3", "a1");
        } else {
            // Reset from a1, a2, a3
            runAsserts(moveSelector, "a3", "a1", "a2");
        }
        moveSelector.stepEnded(stepScopeB1);

        moveSelector.phaseEnded(phaseScopeB);

        moveSelector.solvingEnded(solverScope);

        verify(childMoveSelector, times(timesCalled)).iterator();
        verify(childMoveSelector, times(timesCalled)).getSize();
    }

    private void runAsserts(CachingMoveSelector moveSelector, String... codes) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertNotNull(iterator);
        for (String code : codes) {
            assertTrue(iterator.hasNext());
            assertCode(code, iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertEquals(false, moveSelector.isContinuous());
        assertEquals(false, moveSelector.isNeverEnding());
        assertEquals(3L, moveSelector.getSize());
    }

}
