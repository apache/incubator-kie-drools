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

import java.util.Iterator;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.SelectorTestUtils;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataEntity;
import org.junit.Test;
import org.mockito.Matchers;

import static org.drools.planner.core.testdata.util.PlannerAssert.*;
import static org.mockito.Mockito.*;

public class SwapMoveSelectorTest {

    @Test
    public void originalLeftEqualsRight() {
        EntitySelector entitySelector  = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
                new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));

        SwapMoveSelector moveSelector = new SwapMoveSelector(entitySelector, entitySelector,
                entitySelector.getEntityDescriptor().getPlanningVariableDescriptors(), false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        runAssertsOriginalLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        runAssertsOriginalLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        runAssertsOriginalLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        runAssertsOriginalLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        runAssertsOriginalLeftEqualsRight(moveSelector);
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

    private void runAssertsOriginalLeftEqualsRight(SwapMoveSelector moveSelector) {
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
    public void emptyOriginalLeftEqualsRight() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);

        SwapMoveSelector moveSelector = new SwapMoveSelector(entitySelector, entitySelector,
                entitySelector.getEntityDescriptor().getPlanningVariableDescriptors(), false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        runAssertsEmptyOriginalLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        runAssertsEmptyOriginalLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        runAssertsEmptyOriginalLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        runAssertsEmptyOriginalLeftEqualsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        runAssertsEmptyOriginalLeftEqualsRight(moveSelector);
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

    private void runAssertsEmptyOriginalLeftEqualsRight(SwapMoveSelector moveSelector) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
        assertEquals(false, moveSelector.isContinuous());
        assertEquals(false, moveSelector.isNeverEnding());
        assertEquals(0L, moveSelector.getSize());
    }

    @Test
    public void originalLeftUnequalsRight() {
        PlanningEntityDescriptor entityDescriptor = mock(PlanningEntityDescriptor.class);
        when(entityDescriptor.getPlanningEntityClass()).thenReturn((Class) TestdataEntity.class);

        EntitySelector leftEntitySelector  = SelectorTestUtils.mockEntitySelector(entityDescriptor,
                new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));

        EntitySelector rightEntitySelector  = SelectorTestUtils.mockEntitySelector(entityDescriptor,
                new TestdataEntity("x"), new TestdataEntity("y"), new TestdataEntity("z"));

        SwapMoveSelector moveSelector = new SwapMoveSelector(leftEntitySelector, rightEntitySelector,
                leftEntitySelector.getEntityDescriptor().getPlanningVariableDescriptors(), false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        runAssertsOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        runAssertsOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        runAssertsOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        runAssertsOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        runAssertsOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB3);

        moveSelector.phaseEnded(phaseScopeB);

        moveSelector.solvingEnded(solverScope);

        verify(leftEntitySelector, times(1)).solvingStarted(solverScope);
        verify(leftEntitySelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
        verify(leftEntitySelector, times(5)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(leftEntitySelector, times(5)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(leftEntitySelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
        verify(leftEntitySelector, times(1)).solvingEnded(solverScope);
        verify(rightEntitySelector, times(1)).solvingStarted(solverScope);
        verify(rightEntitySelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
        verify(rightEntitySelector, times(5)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(rightEntitySelector, times(5)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(rightEntitySelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
        verify(rightEntitySelector, times(1)).solvingEnded(solverScope);
    }

    private void runAssertsOriginalLeftUnequalsRight(SwapMoveSelector moveSelector) {
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

    @Test
    public void emptyRightOriginalLeftUnequalsRight() {
        PlanningEntityDescriptor entityDescriptor = mock(PlanningEntityDescriptor.class);
        when(entityDescriptor.getPlanningEntityClass()).thenReturn((Class) TestdataEntity.class);

        EntitySelector leftEntitySelector = SelectorTestUtils.mockEntitySelector(entityDescriptor,
                new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"), new TestdataEntity("d"));

        EntitySelector rightEntitySelector = SelectorTestUtils.mockEntitySelector(entityDescriptor);

        SwapMoveSelector moveSelector = new SwapMoveSelector(leftEntitySelector, rightEntitySelector,
                leftEntitySelector.getEntityDescriptor().getPlanningVariableDescriptors(), false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        runAssertsEmptyRightOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        runAssertsEmptyRightOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        runAssertsEmptyRightOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        runAssertsEmptyRightOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        runAssertsEmptyRightOriginalLeftUnequalsRight(moveSelector);
        moveSelector.stepEnded(stepScopeB3);

        moveSelector.phaseEnded(phaseScopeB);

        moveSelector.solvingEnded(solverScope);

        verify(leftEntitySelector, times(1)).solvingStarted(solverScope);
        verify(leftEntitySelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
        verify(leftEntitySelector, times(5)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(leftEntitySelector, times(5)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(leftEntitySelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
        verify(leftEntitySelector, times(1)).solvingEnded(solverScope);
        verify(rightEntitySelector, times(1)).solvingStarted(solverScope);
        verify(rightEntitySelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
        verify(rightEntitySelector, times(5)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(rightEntitySelector, times(5)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(rightEntitySelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
        verify(rightEntitySelector, times(1)).solvingEnded(solverScope);
    }

    private void runAssertsEmptyRightOriginalLeftUnequalsRight(SwapMoveSelector moveSelector) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
        assertEquals(false, moveSelector.isContinuous());
        assertEquals(false, moveSelector.isNeverEnding());
        assertEquals(0L, moveSelector.getSize());
    }

    private void assertNextSwapMove(Iterator<Move> iterator, String leftEntityCode, String rightEntityCode) {
        assertTrue(iterator.hasNext());
        SwapMove move = (SwapMove) iterator.next();
        assertCode(leftEntityCode, move.getLeftPlanningEntity());
        assertCode(rightEntityCode, move.getRightPlanningEntity());
    }

}
