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

package org.drools.planner.core.heuristic.selector.move.generic;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.generic.GenericSwapMove;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataEntity;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.drools.planner.core.testdata.util.PlannerAssert.*;
import static org.mockito.Mockito.*;

public class SwapMoveSelectorTest {

    @Test
    public void nonrandomLeftEqualsRight() {
        PlanningEntityDescriptor entityDescriptor = mock(PlanningEntityDescriptor.class);
        when(entityDescriptor.getPlanningEntityClass()).thenReturn((Class) TestdataEntity.class);
        EntitySelector entitySelector = mock(EntitySelector.class);
        final List<Object> entityList = Arrays.<Object>asList(
                new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));
        when(entitySelector.getEntityDescriptor()).thenReturn(entityDescriptor);
        when(entitySelector.iterator()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return entityList.iterator();
            }
        });
        when(entitySelector.listIterator()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return entityList.listIterator();
            }
        });
        for (int i = 0; i < entityList.size(); i++) {
            final int index = i;
            when(entitySelector.listIterator(index)).thenAnswer(new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    return entityList.listIterator(index);
                }
            });
        }
        when(entitySelector.isContinuous()).thenReturn(false);
        when(entitySelector.isNeverEnding()).thenReturn(false);
        when(entitySelector.getSize()).thenReturn((long) entityList.size());

        SwapMoveSelector moveSelector = new SwapMoveSelector(entitySelector, entitySelector, false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        runAssertsNonrandomLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        runAssertsNonrandomLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        runAssertsNonrandomLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        runAssertsNonrandomLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        runAssertsNonrandomLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB3);

        moveSelector.phaseEnded(phaseScopeB);

        moveSelector.solvingEnded(solverScope);

        verify(entitySelector, times(1)).solvingStarted(solverScope);
        verify(entitySelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
        verify(entitySelector, times(5)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(entitySelector, times(5)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(entitySelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
        verify(entitySelector, times(1)).solvingEnded(solverScope);
    }

    private void runAssertsNonrandomLeftEqualsRight(SwapMoveSelector moveSelector) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertNotNull(iterator);
        assertNextSwapMove(iterator, "a", "b");
        assertNextSwapMove(iterator, "a", "c");
        assertNextSwapMove(iterator, "a", "d");
        assertNextSwapMove(iterator, "b", "c");
        assertNextSwapMove(iterator, "b", "d");
        assertNextSwapMove(iterator, "c", "d");
        assertFalse(iterator.hasNext());
        assertEquals(false, moveSelector.isContinuous());
        assertEquals(false, moveSelector.isNeverEnding());
        assertEquals(6L, moveSelector.getSize());
    }

    @Test
    public void nonrandomLeftUnequalsRight() {
        PlanningEntityDescriptor entityDescriptor = mock(PlanningEntityDescriptor.class);
        when(entityDescriptor.getPlanningEntityClass()).thenReturn((Class) TestdataEntity.class);

        EntitySelector leftEntitySelector = mock(EntitySelector.class);
        final List<Object> leftEntityList = Arrays.<Object>asList(
                new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));
        when(leftEntitySelector.getEntityDescriptor()).thenReturn(entityDescriptor);
        when(leftEntitySelector.iterator()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return leftEntityList.iterator();
            }
        });
        when(leftEntitySelector.listIterator()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return leftEntityList.listIterator();
            }
        });
        for (int i = 0; i < leftEntityList.size(); i++) {
            final int index = i;
            when(leftEntitySelector.listIterator(index)).thenAnswer(new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    return leftEntityList.listIterator(index);
                }
            });
        }
        when(leftEntitySelector.isContinuous()).thenReturn(false);
        when(leftEntitySelector.isNeverEnding()).thenReturn(false);
        when(leftEntitySelector.getSize()).thenReturn((long) leftEntityList.size());

        EntitySelector rightEntitySelector = mock(EntitySelector.class);
        final List<Object> rightEntityList = Arrays.<Object>asList(
                new TestdataEntity("x"), new TestdataEntity("y"), new TestdataEntity("z"));
        when(rightEntitySelector.getEntityDescriptor()).thenReturn(entityDescriptor);
        when(rightEntitySelector.iterator()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return rightEntityList.iterator();
            }
        });
        when(rightEntitySelector.listIterator()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return rightEntityList.listIterator();
            }
        });
        for (int i = 0; i < rightEntityList.size(); i++) {
            final int index = i;
            when(rightEntitySelector.listIterator(index)).thenAnswer(new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    return rightEntityList.listIterator(index);
                }
            });
        }
        when(rightEntitySelector.isContinuous()).thenReturn(false);
        when(rightEntitySelector.isNeverEnding()).thenReturn(false);
        when(rightEntitySelector.getSize()).thenReturn((long) rightEntityList.size());

        SwapMoveSelector moveSelector = new SwapMoveSelector(leftEntitySelector, rightEntitySelector, false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        runAssertsNonrandomLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        runAssertsNonrandomLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        runAssertsNonrandomLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        runAssertsNonrandomLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        runAssertsNonrandomLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB3);

        moveSelector.phaseEnded(phaseScopeB);

        moveSelector.solvingEnded(solverScope);

        verify(leftEntitySelector, times(1)).solvingStarted(solverScope);
        verify(leftEntitySelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
        verify(leftEntitySelector, times(5)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(leftEntitySelector, times(5)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(leftEntitySelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
        verify(leftEntitySelector, times(1)).solvingEnded(solverScope);
    }

    private void runAssertsNonrandomLeftUnequalsRight(SwapMoveSelector moveSelector) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertNotNull(iterator);
        assertNextSwapMove(iterator, "a", "x");
        assertNextSwapMove(iterator, "a", "y");
        assertNextSwapMove(iterator, "a", "z");
        assertNextSwapMove(iterator, "b", "x");
        assertNextSwapMove(iterator, "b", "y");
        assertNextSwapMove(iterator, "b", "z");
        assertNextSwapMove(iterator, "c", "x");
        assertNextSwapMove(iterator, "c", "y");
        assertNextSwapMove(iterator, "c", "z");
        assertNextSwapMove(iterator, "d", "x");
        assertNextSwapMove(iterator, "d", "y");
        assertNextSwapMove(iterator, "d", "z");
        assertFalse(iterator.hasNext());
        assertEquals(false, moveSelector.isContinuous());
        assertEquals(false, moveSelector.isNeverEnding());
        assertEquals(12L, moveSelector.getSize());
    }

    private void assertNextSwapMove(Iterator<Move> iterator, String leftEntityCode, String rightEntityCode) {
        assertTrue(iterator.hasNext());
        GenericSwapMove move = (GenericSwapMove) iterator.next();
        assertCode(leftEntityCode, move.getLeftPlanningEntity());
        assertCode(rightEntityCode, move.getRightPlanningEntity());
    }

}
