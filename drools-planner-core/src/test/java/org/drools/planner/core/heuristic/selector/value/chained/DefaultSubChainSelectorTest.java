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

package org.drools.planner.core.heuristic.selector.value.chained;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.SelectorTestUtils;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataChainedAnchor;
import org.drools.planner.core.testdata.domain.TestdataChainedEntity;
import org.junit.Test;
import org.mockito.Matchers;

import static org.drools.planner.core.testdata.util.PlannerAssert.*;
import static org.mockito.Mockito.*;

public class DefaultSubChainSelectorTest {

    @Test
    public void original() {
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        PlanningVariableDescriptor variableDescriptor = SelectorTestUtils.mockVariableDescriptor(
                TestdataChainedEntity.class, "chainedObject");
        when(variableDescriptor.isChained()).thenReturn(true);

        ValueSelector valueSelector = SelectorTestUtils.mockValueSelector(variableDescriptor,
                a0, a1, a2, a3, a4, b0, b1, b2);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(valueSelector, false);

        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a0)).thenReturn(a1);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a1)).thenReturn(a2);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a2)).thenReturn(a3);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a3)).thenReturn(a4);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a4)).thenReturn(null);
        when(scoreDirector.getTrailingEntity(variableDescriptor, b0)).thenReturn(b1);
        when(scoreDirector.getTrailingEntity(variableDescriptor, b1)).thenReturn(b2);
        when(scoreDirector.getTrailingEntity(variableDescriptor, b2)).thenReturn(null);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        subChainSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);
        runAssertsOriginal1(subChainSelector);
        subChainSelector.stepEnded(stepScopeA1);

        a4.setChainedObject(a2);
        a3.setChainedObject(b1);
        b2.setChainedObject(a3);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a2)).thenReturn(a4);
        when(scoreDirector.getTrailingEntity(variableDescriptor, b1)).thenReturn(a3);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a3)).thenReturn(b2);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA2);
        runAssertsOriginal2(subChainSelector);
        subChainSelector.stepEnded(stepScopeA2);

        subChainSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        subChainSelector.stepStarted(stepScopeB1);
        runAssertsOriginal2(subChainSelector);
        subChainSelector.stepEnded(stepScopeB1);

        subChainSelector.phaseEnded(phaseScopeB);

        subChainSelector.solvingEnded(solverScope);

        verify(valueSelector, times(1)).solvingStarted(solverScope);
        verify(valueSelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
        verify(valueSelector, times(3)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(valueSelector, times(3)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(valueSelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
        verify(valueSelector, times(1)).solvingEnded(solverScope);
    }

    private void runAssertsOriginal1(DefaultSubChainSelector subChainSelector) {
        Iterator<SubChain> iterator = subChainSelector.iterator();
        assertNotNull(iterator);
        assertNextSubChain(iterator, "a1");
        assertNextSubChain(iterator, "a1", "a2");
        assertNextSubChain(iterator, "a1", "a2", "a3");
        assertNextSubChain(iterator, "a1", "a2", "a3", "a4");
        assertNextSubChain(iterator, "a2");
        assertNextSubChain(iterator, "a2", "a3");
        assertNextSubChain(iterator, "a2", "a3", "a4");
        assertNextSubChain(iterator, "a3");
        assertNextSubChain(iterator, "a3", "a4");
        assertNextSubChain(iterator, "a4");
        assertNextSubChain(iterator, "b1");
        assertNextSubChain(iterator, "b1", "b2");
        assertNextSubChain(iterator, "b2");
        assertFalse(iterator.hasNext());
        assertEquals(false, subChainSelector.isContinuous());
        assertEquals(false, subChainSelector.isNeverEnding());
        assertEquals(13L, subChainSelector.getSize());
    }

    private void runAssertsOriginal2(DefaultSubChainSelector subChainSelector) {
        Iterator<SubChain> iterator = subChainSelector.iterator();
        assertNotNull(iterator);
        assertNextSubChain(iterator, "a1");
        assertNextSubChain(iterator, "a1", "a2");
        assertNextSubChain(iterator, "a1", "a2", "a4");
        assertNextSubChain(iterator, "a2");
        assertNextSubChain(iterator, "a2", "a4");
        assertNextSubChain(iterator, "a4");
        assertNextSubChain(iterator, "b1");
        assertNextSubChain(iterator, "b1", "a3");
        assertNextSubChain(iterator, "b1", "a3", "b2");
        assertNextSubChain(iterator, "a3");
        assertNextSubChain(iterator, "a3", "b2");
        assertNextSubChain(iterator, "b2");
        assertFalse(iterator.hasNext());
        assertEquals(false, subChainSelector.isContinuous());
        assertEquals(false, subChainSelector.isNeverEnding());
        assertEquals(12L, subChainSelector.getSize());
    }

//    @Test
//    public void emptySelectorOriginal() {
//        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
//        ValueSelector valueSelector = SelectorTestUtils.mockValueSelector(TestdataEntity.class, "value",
//                new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));
//
//        DefaultSubChainSelector moveSelector = new DefaultSubChainSelector(entitySelector, valueSelector, false);
//
//        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
//        moveSelector.solvingStarted(solverScope);
//
//        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
//        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
//        moveSelector.phaseStarted(phaseScopeA);
//
//        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
//        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
//        moveSelector.stepStarted(stepScopeA1);
//        runAssertsEmptyOriginal(moveSelector);
//        moveSelector.stepEnded(stepScopeA1);
//
//        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
//        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
//        moveSelector.stepStarted(stepScopeA2);
//        runAssertsEmptyOriginal(moveSelector);
//        moveSelector.stepEnded(stepScopeA2);
//
//        moveSelector.phaseEnded(phaseScopeA);
//
//        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
//        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
//        moveSelector.phaseStarted(phaseScopeB);
//
//        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
//        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
//        moveSelector.stepStarted(stepScopeB1);
//        runAssertsEmptyOriginal(moveSelector);
//        moveSelector.stepEnded(stepScopeB1);
//
//        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
//        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
//        moveSelector.stepStarted(stepScopeB2);
//        runAssertsEmptyOriginal(moveSelector);
//        moveSelector.stepEnded(stepScopeB2);
//
//        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
//        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
//        moveSelector.stepStarted(stepScopeB3);
//        runAssertsEmptyOriginal(moveSelector);
//        moveSelector.stepEnded(stepScopeB3);
//
//        moveSelector.phaseEnded(phaseScopeB);
//
//        moveSelector.solvingEnded(solverScope);
//
//        verify(entitySelector, times(1)).solvingStarted(solverScope);
//        verify(entitySelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
//        verify(entitySelector, times(5)).stepStarted(Matchers.<AbstractStepScope>any());
//        verify(entitySelector, times(5)).stepEnded(Matchers.<AbstractStepScope>any());
//        verify(entitySelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
//        verify(entitySelector, times(1)).solvingEnded(solverScope);
//    }
//
//    private void runAssertsEmptyOriginal(DefaultSubChainSelector moveSelector) {
//        Iterator<Move> iterator = moveSelector.iterator();
//        assertNotNull(iterator);
//        assertFalse(iterator.hasNext());
//        assertEquals(false, moveSelector.isContinuous());
//        assertEquals(false, moveSelector.isNeverEnding());
//        assertEquals(0L, moveSelector.getSize());
//    }

    private void assertNextSubChain(Iterator<SubChain> iterator, String... valueCodes) {
        assertTrue(iterator.hasNext());
        SubChain subChain = iterator.next();
        List<Object> valueList = subChain.getValueList();
        String message = "Expected valueCodes (" + Arrays.toString(valueCodes)
                + ") but received valueList (" + valueList + ").";
        assertEquals(message, valueCodes.length, valueList.size());
        for (int i = 0; i < valueCodes.length; i++) {
            assertCode(message, valueCodes[i], valueList.get(i));
        }
    }

}
