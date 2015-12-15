/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class DefaultPillarSelectorTest {

//    @Test
//    public void calculateBasePillarSelectionSize() {
//        assertCalculateBasePillarSelectionSize(4L, 1, 1);
//        assertCalculateBasePillarSelectionSize(6L, 2, 2);
//        assertCalculateBasePillarSelectionSize(4L, 3, 3);
//        assertCalculateBasePillarSelectionSize(1L, 4, 4);
//
//        assertCalculateBasePillarSelectionSize(10L, 1, 2);
//        assertCalculateBasePillarSelectionSize(14L, 1, 3);
//        assertCalculateBasePillarSelectionSize(15L, 1, 4);
//        assertCalculateBasePillarSelectionSize(10L, 2, 3);
//        assertCalculateBasePillarSelectionSize(11L, 2, 4);
//        assertCalculateBasePillarSelectionSize(5L, 3, 4);
//
//        assertCalculateBasePillarSelectionSize(15L, 1, 5);
//        assertCalculateBasePillarSelectionSize(11L, 2, 5);
//        assertCalculateBasePillarSelectionSize(5L, 3, 5);
//        assertCalculateBasePillarSelectionSize(1L, 4, 5);
//        assertCalculateBasePillarSelectionSize(0L, 5, 5);
//    }
//
//    private void assertCalculateBasePillarSelectionSize(long expected, int minimumPillarSize, int maximumPillarSize) {
//        TestdataValue val1 = new TestdataValue("1");
//        TestdataValue val2 = new TestdataValue("2");
//        TestdataValue val3 = new TestdataValue("3");
//        TestdataValue val4 = new TestdataValue("4");
//
//        final TestdataEntity a = new TestdataEntity("a", val1);
//        final TestdataEntity b = new TestdataEntity("b", val2);
//        final TestdataEntity c = new TestdataEntity("c", val3);
//        final TestdataEntity d = new TestdataEntity("d", val2);
//
//        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
//                a, b, c, d);
//        GenuineVariableDescriptor variableDescriptor = SelectorTestUtils.mockVariableDescriptor(
//                entitySelector.findEntityDescriptorOrFail(), "value");
//        DefaultPillarSelector selector = new DefaultPillarSelector(
//                entitySelector, Collections.singletonList(variableDescriptor), false,
//                true, minimumPillarSize, maximumPillarSize);
//        assertEquals(expected, selector.calculateBasePillarSelectionSize(Arrays.<Object>asList(a, b, c, d)));
//    }

    @Test
    public void originalNoSubs() {
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

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                a, b, c, d, e, f);

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), false, false, 1, Integer.MAX_VALUE);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
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

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        assertAllCodesOfPillarSelector(pillarSelector, "[a]", "[b, c, e]", "[d]", "[f]");
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    @Test
    public void emptyEntitySelectorOriginalNoSubs() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor());

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), false, false, 1, Integer.MAX_VALUE);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
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

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        assertAllCodesOfPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    @Test
    public void randomWithSubs() {
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

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                a, b, c, d, e, f);

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), true, true, 1, Integer.MAX_VALUE);

        Random workingRandom = mock(Random.class);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        // nextInt pattern: pillarIndex, subPillarSize, element 0, element 1, element 2, ...
        // Expected pillar cache: [a], [b, d], [c, e, f]
        when(workingRandom.nextInt(anyInt())).thenReturn(
                0, 0, 0, // [a]
                2, 1, 0, 0, // [c, e, f]
                1, 0, 0, // [b, d]
                1, 0, 1); // [b, d]
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[a]", "[c, e]", "[b]", "[d]");
        pillarSelector.stepEnded(stepScopeA1);

        b.setValue(val3);
        f.setValue(val4);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        // nextInt pattern: pillarIndex, subPillarSize, element 0, element 1, element 2, ...
        // Expected pillar cache: [a], [b, c, e], [d], [f]
        when(workingRandom.nextInt(anyInt())).thenReturn(
                3, 0, 0, // [f]
                1, 2, 2, 0, 0, // [b, c, e]
                1, 0, 0, // [b, c, e]
                2, 0, 0); // [d]
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[f]", "[e, c, b]", "[b]", "[d]");
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        // nextInt pattern: pillarIndex, subPillarSize, element 0, element 1, element 2, ...
        // Expected pillar cache: [a], [b, c, e], [d], [f]
        when(workingRandom.nextInt(anyInt())).thenReturn(
                3, 0, 0, // [f]
                1, 2, 2, 0, 0, // [b, c, e]
                1, 0, 0, // [b, c, e]
                2, 0, 0); // [d]
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[f]", "[e, c, b]", "[b]", "[d]");
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    @Test
    public void randomWithSubs_Size2() {
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

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                a, b, c, d, e, f);

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), true, true, 2, 2);

        Random workingRandom = mock(Random.class);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        // nextInt pattern: pillarIndex, subPillarSize, element 0, element 1, element 2, ...
        // Expected pillar cache: [a], [b, d], [c, e, f]
        when(workingRandom.nextInt(anyInt())).thenReturn(
                0, 0, 0, // [a] If the pillar is less than the minimum, the minimum is reduced
                2, 0, 0, 0, // [c, e, f]
                1, 0, 0, 0, // [b, d]
                1, 0, 1, 0); // [b, d]
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[a]", "[c, e]", "[b, d]", "[d, b]");
        pillarSelector.stepEnded(stepScopeA1);

        pillarSelector.phaseEnded(phaseScopeA);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 1);
    }

    @Test
    public void emptyEntitySelectorRandomWithSubs() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor());

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), true, true, 1, Integer.MAX_VALUE);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        assertCodesOfNeverEndingPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        assertCodesOfNeverEndingPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        assertCodesOfNeverEndingPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    private void assertAllCodesOfPillarSelector(PillarSelector pillarSelector, String... codes) {
        assertAllCodesOfIterator(pillarSelector.iterator(), codes);
        assertEquals(true, pillarSelector.isCountable());
        assertEquals(false, pillarSelector.isNeverEnding());
        assertEquals(codes.length, pillarSelector.getSize());
    }

    private void assertCodesOfNeverEndingPillarSelector(PillarSelector pillarSelector, String... codes) {
        assertCodesOfIterator(pillarSelector.iterator(), codes);
        assertEquals(true, pillarSelector.isCountable());
        assertEquals(true, pillarSelector.isNeverEnding());
    }

}
