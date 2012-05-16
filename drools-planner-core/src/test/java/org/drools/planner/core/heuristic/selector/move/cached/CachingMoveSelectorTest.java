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

import org.drools.planner.core.heuristic.selector.common.SelectorCacheType;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CachingMoveSelectorTest {

    @Test
    public void cacheTypeSolver() {
        runCacheType(SelectorCacheType.SOLVER, 1);
    }

    @Test
    public void cacheTypePhase() {
        runCacheType(SelectorCacheType.PHASE, 2);
    }

    @Test
    public void cacheTypeStep() {
        runCacheType(SelectorCacheType.STEP, 5);
    }

    public void runCacheType(SelectorCacheType cacheType, int timesCalled) {
        CachingMoveSelector moveSelector = new CachingMoveSelector(cacheType);
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
        when(childMoveSelector.getRandomProbabilityWeight()).thenReturn(7L);
        moveSelector.setChildMoveSelector(childMoveSelector);
        verify(childMoveSelector, times(1)).isNeverEnding();

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        runAsserts(moveSelector);
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        runAsserts(moveSelector);
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        runAsserts(moveSelector);
        moveSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        runAsserts(moveSelector);
        moveSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        runAsserts(moveSelector);
        moveSelector.stepEnded(stepScopeB3);

        moveSelector.phaseEnded(phaseScopeB);

        moveSelector.solvingEnded(solverScope);

        verify(childMoveSelector, times(timesCalled)).iterator();
        verify(childMoveSelector, times(timesCalled)).getSize();
        verify(childMoveSelector, times(timesCalled)).getRandomProbabilityWeight();
    }

    private void runAsserts(CachingMoveSelector moveSelector) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertCode("a1", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("a2", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("a3", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(false, moveSelector.isContinuous());
        assertEquals(false, moveSelector.isNeverEnding());
        assertEquals(3L, moveSelector.getSize());
        assertEquals(7L, moveSelector.getRandomProbabilityWeight());
    }

}
