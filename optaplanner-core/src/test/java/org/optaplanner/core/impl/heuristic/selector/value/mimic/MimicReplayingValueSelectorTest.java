/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.value.mimic;

import java.util.Iterator;

import org.junit.Test;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class MimicReplayingValueSelectorTest {

    @Test
    public void originalSelection() {
        EntityIndependentValueSelector childValueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                TestdataEntity.class, "value",
                new TestdataValue("v1"), new TestdataValue("v2"), new TestdataValue("v3"));

        MimicRecordingValueSelector recordingValueSelector = new MimicRecordingValueSelector(childValueSelector);
        MimicReplayingValueSelector replayingValueSelector = new MimicReplayingValueSelector(recordingValueSelector);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        recordingValueSelector.solvingStarted(solverScope);
        replayingValueSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        recordingValueSelector.phaseStarted(phaseScopeA);
        replayingValueSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        recordingValueSelector.stepStarted(stepScopeA1);
        replayingValueSelector.stepStarted(stepScopeA1);
        runOriginalAsserts(recordingValueSelector, replayingValueSelector);
        recordingValueSelector.stepEnded(stepScopeA1);
        replayingValueSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        recordingValueSelector.stepStarted(stepScopeA2);
        replayingValueSelector.stepStarted(stepScopeA2);
        runOriginalAsserts(recordingValueSelector, replayingValueSelector);
        recordingValueSelector.stepEnded(stepScopeA2);
        replayingValueSelector.stepEnded(stepScopeA2);

        recordingValueSelector.phaseEnded(phaseScopeA);
        replayingValueSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        recordingValueSelector.phaseStarted(phaseScopeB);
        replayingValueSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        recordingValueSelector.stepStarted(stepScopeB1);
        replayingValueSelector.stepStarted(stepScopeB1);
        runOriginalAsserts(recordingValueSelector, replayingValueSelector);
        recordingValueSelector.stepEnded(stepScopeB1);
        replayingValueSelector.stepEnded(stepScopeB1);

        recordingValueSelector.phaseEnded(phaseScopeB);
        replayingValueSelector.phaseEnded(phaseScopeB);

        recordingValueSelector.solvingEnded(solverScope);
        replayingValueSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childValueSelector, 1, 2, 3);
        verify(childValueSelector, times(3)).iterator();
    }

    private void runOriginalAsserts(MimicRecordingValueSelector recordingValueSelector,
            MimicReplayingValueSelector replayingValueSelector) {
        Iterator<Object> recordingIterator = recordingValueSelector.iterator();
        assertNotNull(recordingIterator);
        Iterator<Object> replayingIterator = replayingValueSelector.iterator();
        assertNotNull(replayingIterator);

        assertEquals(true, recordingIterator.hasNext());
        assertEquals(true, replayingIterator.hasNext());
        assertCode("v1", recordingIterator.next());
        assertCode("v1", replayingIterator.next());
        assertEquals(true, recordingIterator.hasNext());
        assertEquals(true, replayingIterator.hasNext());
        assertCode("v2", recordingIterator.next());
        assertCode("v2", replayingIterator.next());
        assertEquals(false, replayingIterator.hasNext()); // Extra call
        assertEquals(true, recordingIterator.hasNext());
        assertEquals(true, replayingIterator.hasNext());
        assertEquals(true, replayingIterator.hasNext()); // Duplicated call
        assertCode("v3", recordingIterator.next());
        assertCode("v3", replayingIterator.next());
        assertEquals(false, recordingIterator.hasNext());
        assertEquals(false, replayingIterator.hasNext());
        assertEquals(false, replayingIterator.hasNext()); // Duplicated call

        assertEquals(true, recordingValueSelector.isCountable());
        assertEquals(true, replayingValueSelector.isCountable());
        assertEquals(false, recordingValueSelector.isNeverEnding());
        assertEquals(false, replayingValueSelector.isNeverEnding());
        assertEquals(3L, recordingValueSelector.getSize());
        assertEquals(3L, replayingValueSelector.getSize());
    }

}
