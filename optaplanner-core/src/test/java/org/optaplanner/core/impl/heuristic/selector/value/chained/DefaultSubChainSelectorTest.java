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

package org.optaplanner.core.impl.heuristic.selector.value.chained;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.junit.Test;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.mockito.Mockito.*;

public class DefaultSubChainSelectorTest {

    @Test
    public void original() {
        PlanningVariableDescriptor variableDescriptor = SelectorTestUtils.mockVariableDescriptor(
                TestdataChainedEntity.class, "chainedObject");
        when(variableDescriptor.isChained()).thenReturn(true);
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, a4, b1, b2});

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, a1, a2, a3, a4, b0, b1, b2);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, false, 1, Integer.MAX_VALUE);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        subChainSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);
        runAssertsOriginal1(subChainSelector);
        subChainSelector.stepEnded(stepScopeA1);

        a4.setChainedObject(a2);
        a3.setChainedObject(b1);
        b2.setChainedObject(a3);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA2);
        runAssertsOriginal2(subChainSelector);
        subChainSelector.stepEnded(stepScopeA2);

        subChainSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        subChainSelector.stepStarted(stepScopeB1);
        runAssertsOriginal2(subChainSelector);
        subChainSelector.stepEnded(stepScopeB1);

        subChainSelector.phaseEnded(phaseScopeB);

        subChainSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(valueSelector, 1, 2, 3);
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

    @Test
    public void emptyEntitySelectorOriginal() {
        PlanningVariableDescriptor variableDescriptor = SelectorTestUtils.mockVariableDescriptor(
                TestdataChainedEntity.class, "chainedObject");
        when(variableDescriptor.isChained()).thenReturn(true);
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{});

        EntityIndependentValueSelector valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                a0, b0);

        DefaultSubChainSelector subChainSelector = new DefaultSubChainSelector(
                valueSelector, false, 1, Integer.MAX_VALUE);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);
        subChainSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA1);
        runAssertsEmptyOriginal(subChainSelector);
        subChainSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        subChainSelector.stepStarted(stepScopeA2);
        runAssertsEmptyOriginal(subChainSelector);
        subChainSelector.stepEnded(stepScopeA2);

        subChainSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        subChainSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        subChainSelector.stepStarted(stepScopeB1);
        runAssertsEmptyOriginal(subChainSelector);
        subChainSelector.stepEnded(stepScopeB1);

        subChainSelector.phaseEnded(phaseScopeB);

        subChainSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(valueSelector, 1, 2, 3);
    }

    private void runAssertsEmptyOriginal(DefaultSubChainSelector subChainSelector) {
        Iterator<SubChain> iterator = subChainSelector.iterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
        assertEquals(false, subChainSelector.isContinuous());
        assertEquals(false, subChainSelector.isNeverEnding());
        assertEquals(0L, subChainSelector.getSize());
    }

    private void assertNextSubChain(Iterator<SubChain> iterator, String... entityCodes) {
        assertTrue(iterator.hasNext());
        SubChain subChain = iterator.next();
        List<Object> entityList = subChain.getEntityList();
        String message = "Expected entityCodes (" + Arrays.toString(entityCodes)
                + ") but received entityList (" + entityList + ").";
        assertEquals(message, entityCodes.length, entityList.size());
        for (int i = 0; i < entityCodes.length; i++) {
            assertCode(message, entityCodes[i], entityList.get(i));
        }
    }

}
