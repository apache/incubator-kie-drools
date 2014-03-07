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

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

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
        GenuineVariableDescriptor variableDescriptor = SelectorTestUtils.mockVariableDescriptor(
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
        assertAllCodesOfPillarSelector(pillarSelector, "[a]", "[b, d]", "[c, e, f]");
        pillarSelector.stepEnded(stepScopeA1);

        b.setValue(val3);
        f.setValue(val4);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        assertAllCodesOfPillarSelector(pillarSelector, "[a]", "[b, c, e]", "[d]", "[f]");
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        assertAllCodesOfPillarSelector(pillarSelector, "[a]", "[b, c, e]", "[d]", "[f]");
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    @Test
    public void emptyEntitySelectorOriginal() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class);
        GenuineVariableDescriptor variableDescriptor = SelectorTestUtils.mockVariableDescriptor(
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
        assertAllCodesOfPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        assertAllCodesOfPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        assertAllCodesOfPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    private void assertAllCodesOfPillarSelector(PillarSelector pillarSelector, String... codes) {
        assertAllCodesOfIterator(pillarSelector.iterator(), codes);
        assertEquals(true, pillarSelector.isCountable());
        assertEquals(false, pillarSelector.isNeverEnding());
        assertEquals(codes.length, pillarSelector.getSize());
    }

}
