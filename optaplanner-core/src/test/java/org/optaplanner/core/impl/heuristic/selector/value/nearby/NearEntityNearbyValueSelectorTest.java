/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfValueSelectorForEntity;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.ManualEntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;

public class NearEntityNearbyValueSelectorTest {

    @Test
    public void originalSelection() {
        final TestdataEntity africa = new TestdataEntity("Africa");
        final TestdataEntity europe = new TestdataEntity("Europe");
        final TestdataEntity oceania = new TestdataEntity("Oceania");
        final TestdataValue morocco = new TestdataValue("Morocco");
        final TestdataValue spain = new TestdataValue("Spain");
        final TestdataValue australia = new TestdataValue("Australia");
        final TestdataValue brazil = new TestdataValue("Brazil");

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntityIndependentValueSelector childValueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                morocco, spain, australia, brazil);
        NearbyDistanceMeter<TestdataEntity, TestdataValue> meter = (origin, destination) -> {
            if (origin == africa) {
                if (destination == morocco) {
                    return 0.0;
                } else if (destination == spain) {
                    return 1.0;
                } else if (destination == australia) {
                    return 100.0;
                } else if (destination == brazil) {
                    return 50.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == europe) {
                if (destination == morocco) {
                    return 1.0;
                } else if (destination == spain) {
                    return 0.0;
                } else if (destination == australia) {
                    return 101.0;
                } else if (destination == brazil) {
                    return 51.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == oceania) {
                if (destination == morocco) {
                    return 100.0;
                } else if (destination == spain) {
                    return 101.0;
                } else if (destination == australia) {
                    return 0.0;
                } else if (destination == brazil) {
                    return 60.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else {
                throw new IllegalStateException("The origin (" + origin + ") is not implemented.");
            }
        };
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(), africa,
                europe, oceania);
        ManualEntityMimicRecorder entityMimicRecorder = new ManualEntityMimicRecorder(entitySelector);
        NearEntityNearbyValueSelector valueSelector = new NearEntityNearbyValueSelector(
                childValueSelector, new MimicReplayingEntitySelector(entityMimicRecorder), meter, null, false);

        SolverScope solverScope = mock(SolverScope.class);
        valueSelector.solvingStarted(solverScope);

        // The movingEntity can be the same (ChangeMove) or different (SwapMove) as the nearby source
        TestdataEntity movingEntity = europe;

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA1);
        entityMimicRecorder.setRecordedEntity(europe);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Spain", "Morocco", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA2);
        entityMimicRecorder.setRecordedEntity(africa);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Morocco", "Spain", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeA2);

        valueSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB1);
        entityMimicRecorder.setRecordedEntity(africa);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Morocco", "Spain", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB2);
        entityMimicRecorder.setRecordedEntity(oceania);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Australia", "Brazil", "Morocco", "Spain");
        valueSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB3);
        entityMimicRecorder.setRecordedEntity(europe);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Spain", "Morocco", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeB3);

        valueSelector.phaseEnded(phaseScopeB);

        valueSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childValueSelector, 1, 2, 5);
        //        verify(childValueSelector, times(5)).endingIterator(any());
        //        verify(childValueSelector, times(5)).getSize(any());
    }

    @Test
    public void originalSelectionChained() {
        final TestdataChainedEntity morocco = new TestdataChainedEntity("Morocco");
        final TestdataChainedEntity spain = new TestdataChainedEntity("Spain");
        final TestdataChainedEntity australia = new TestdataChainedEntity("Australia");
        final TestdataChainedAnchor brazil = new TestdataChainedAnchor("Brazil");

        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        EntityIndependentValueSelector childValueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                morocco, spain, australia, brazil);
        NearbyDistanceMeter<TestdataChainedEntity, TestdataChainedObject> meter = (origin, destination) -> {
            if (origin == morocco) {
                if (destination == morocco) {
                    return 0.0;
                } else if (destination == spain) {
                    return 1.0;
                } else if (destination == australia) {
                    return 100.0;
                } else if (destination == brazil) {
                    return 50.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == spain) {
                if (destination == morocco) {
                    return 1.0;
                } else if (destination == spain) {
                    return 0.0;
                } else if (destination == australia) {
                    return 101.0;
                } else if (destination == brazil) {
                    return 51.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == australia) {
                if (destination == morocco) {
                    return 100.0;
                } else if (destination == spain) {
                    return 101.0;
                } else if (destination == australia) {
                    return 0.0;
                } else if (destination == brazil) {
                    return 60.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else {
                throw new IllegalStateException("The origin (" + origin + ") is not implemented.");
            }
        };
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(), morocco,
                spain, australia);
        ManualEntityMimicRecorder entityMimicRecorder = new ManualEntityMimicRecorder(entitySelector);
        NearEntityNearbyValueSelector valueSelector = new NearEntityNearbyValueSelector(
                childValueSelector, new MimicReplayingEntitySelector(entityMimicRecorder), meter, null, false);

        SolverScope solverScope = mock(SolverScope.class);
        valueSelector.solvingStarted(solverScope);

        // The movingEntity can be the same (ChangeMove) or different (SwapMove) as the nearby source
        TestdataChainedEntity movingEntity = spain;

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA1);
        entityMimicRecorder.setRecordedEntity(spain);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Morocco", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA2);
        entityMimicRecorder.setRecordedEntity(morocco);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Spain", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeA2);

        valueSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB1);
        entityMimicRecorder.setRecordedEntity(morocco);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Spain", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB2);
        entityMimicRecorder.setRecordedEntity(australia);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Brazil", "Morocco", "Spain");
        valueSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB3);
        entityMimicRecorder.setRecordedEntity(spain);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Morocco", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeB3);

        valueSelector.phaseEnded(phaseScopeB);

        valueSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childValueSelector, 1, 2, 5);
        //        verify(childValueSelector, times(5)).endingIterator(any());
        //        verify(childValueSelector, times(5)).getSize(any());
    }

}
