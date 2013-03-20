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

package org.optaplanner.core.impl.heuristic.selector.entity.pillar;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.mockito.Mockito.*;

public class SameValuePillarSelectorTest {

    @Test
    public void original() {
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataValue val4 = new TestdataValue("4");

        final TestdataEntity a = new TestdataEntity("a", val1);
        final TestdataEntity b = new TestdataEntity("b", val2);
        final TestdataEntity c = new TestdataEntity("c", val3);
        final TestdataEntity d = new TestdataEntity("d", val2);
        final TestdataEntity e = new TestdataEntity("e", val3);
        final TestdataEntity f = new TestdataEntity("f", val3);

        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
                a, b, c, d, e, f);
        PlanningVariableDescriptor variableDescriptor = SelectorTestUtils.mockVariableDescriptor(
                entitySelector.getEntityDescriptor(), "value");
        for (final TestdataEntity entity : Arrays.asList(a, b, c, d, e, f)) {
            when(variableDescriptor.getValue(entity)).thenAnswer(new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    return entity.getValue();
                }
            });
        }

        SameValuePillarSelector pillarSelector = new SameValuePillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        pillarSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        runAssertsOriginal1(pillarSelector);
        pillarSelector.stepEnded(stepScopeA1);

        b.setValue(val3);
        f.setValue(val4);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        runAssertsOriginal2(pillarSelector);
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        runAssertsOriginal2(pillarSelector);
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    private void runAssertsOriginal1(SameValuePillarSelector pillarSelector) {
        Iterator<List<Object>> iterator = pillarSelector.iterator();
        assertNotNull(iterator);
        assertNextPillar(iterator, "a");
        assertNextPillar(iterator, "b", "d");
        assertNextPillar(iterator, "c", "e", "f");
        assertFalse(iterator.hasNext());
        assertEquals(false, pillarSelector.isContinuous());
        assertEquals(false, pillarSelector.isNeverEnding());
        assertEquals(3L, pillarSelector.getSize());
    }

    private void runAssertsOriginal2(SameValuePillarSelector pillarSelector) {
        Iterator<List<Object>> iterator = pillarSelector.iterator();
        assertNotNull(iterator);
        assertNextPillar(iterator, "a");
        assertNextPillar(iterator, "b", "c", "e");
        assertNextPillar(iterator, "d");
        assertNextPillar(iterator, "f");
        assertFalse(iterator.hasNext());
        assertEquals(false, pillarSelector.isContinuous());
        assertEquals(false, pillarSelector.isNeverEnding());
        assertEquals(4L, pillarSelector.getSize());
    }

    @Test
    public void emptyEntitySelectorOriginal() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        PlanningVariableDescriptor variableDescriptor = SelectorTestUtils.mockVariableDescriptor(
                entitySelector.getEntityDescriptor(), "value");

        SameValuePillarSelector pillarSelector = new SameValuePillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        pillarSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        runAssertsEmptyOriginal(pillarSelector);
        pillarSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        runAssertsEmptyOriginal(pillarSelector);
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        runAssertsEmptyOriginal(pillarSelector);
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    private void runAssertsEmptyOriginal(SameValuePillarSelector pillarSelector) {
        Iterator<List<Object>> iterator = pillarSelector.iterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
        assertEquals(false, pillarSelector.isContinuous());
        assertEquals(false, pillarSelector.isNeverEnding());
        assertEquals(0L, pillarSelector.getSize());
    }

    private void assertNextPillar(Iterator<List<Object>> iterator, String... entityCodes) {
        assertTrue(iterator.hasNext());
        List<Object> pillar = iterator.next();
        String message = "Expected entityCodes (" + Arrays.toString(entityCodes)
                + ") but received pillar (" + pillar + ").";
        assertEquals(message, entityCodes.length, pillar.size());
        for (int i = 0; i < entityCodes.length; i++) {
            assertCode(message, entityCodes[i], pillar.get(i));
        }
    }

}
