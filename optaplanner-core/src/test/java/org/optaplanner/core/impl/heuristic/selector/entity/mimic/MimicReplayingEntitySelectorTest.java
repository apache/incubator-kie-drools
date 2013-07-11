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

package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.Iterator;

import org.junit.Test;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class MimicReplayingEntitySelectorTest {

    @Test
    public void originalSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"));

        MimicRecordingEntitySelector mimicRecordingEntitySelector = new MimicRecordingEntitySelector(childEntitySelector);
        MimicReplayingEntitySelector mimicReplayingEntitySelector = new MimicReplayingEntitySelector(mimicRecordingEntitySelector);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        mimicRecordingEntitySelector.solvingStarted(solverScope);
        mimicReplayingEntitySelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        mimicRecordingEntitySelector.phaseStarted(phaseScopeA);
        mimicReplayingEntitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        mimicRecordingEntitySelector.stepStarted(stepScopeA1);
        mimicReplayingEntitySelector.stepStarted(stepScopeA1);
        runOriginalAsserts(mimicRecordingEntitySelector, mimicReplayingEntitySelector);
        mimicRecordingEntitySelector.stepEnded(stepScopeA1);
        mimicReplayingEntitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        mimicRecordingEntitySelector.stepStarted(stepScopeA2);
        mimicReplayingEntitySelector.stepStarted(stepScopeA2);
        runOriginalAsserts(mimicRecordingEntitySelector, mimicReplayingEntitySelector);
        mimicRecordingEntitySelector.stepEnded(stepScopeA2);
        mimicReplayingEntitySelector.stepEnded(stepScopeA2);

        mimicRecordingEntitySelector.phaseEnded(phaseScopeA);
        mimicReplayingEntitySelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        mimicRecordingEntitySelector.phaseStarted(phaseScopeB);
        mimicReplayingEntitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        mimicRecordingEntitySelector.stepStarted(stepScopeB1);
        mimicReplayingEntitySelector.stepStarted(stepScopeB1);
        runOriginalAsserts(mimicRecordingEntitySelector, mimicReplayingEntitySelector);
        mimicRecordingEntitySelector.stepEnded(stepScopeB1);
        mimicReplayingEntitySelector.stepEnded(stepScopeB1);

        mimicRecordingEntitySelector.phaseEnded(phaseScopeB);
        mimicReplayingEntitySelector.phaseEnded(phaseScopeB);

        mimicRecordingEntitySelector.solvingEnded(solverScope);
        mimicReplayingEntitySelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childEntitySelector, 1, 2, 3);
        verify(childEntitySelector, times(3)).iterator();
    }

    private void runOriginalAsserts(
            MimicRecordingEntitySelector mimicRecordingEntitySelector, MimicReplayingEntitySelector mimicReplayingEntitySelector) {
        Iterator<Object> recordingIterator = mimicRecordingEntitySelector.iterator();
        assertNotNull(recordingIterator);
        Iterator<Object> replayingIterator = mimicReplayingEntitySelector.iterator();
        assertNotNull(replayingIterator);

        assertTrue(recordingIterator.hasNext());
        assertTrue(replayingIterator.hasNext());
        assertCode("e1", recordingIterator.next());
        assertCode("e1", replayingIterator.next());
        assertTrue(recordingIterator.hasNext());
        assertTrue(replayingIterator.hasNext());
        assertCode("e2", recordingIterator.next());
        assertCode("e2", replayingIterator.next());
        assertCode("e2", replayingIterator.next()); // Duplicated call
        assertTrue(recordingIterator.hasNext());
        assertTrue(replayingIterator.hasNext());
        assertTrue(replayingIterator.hasNext()); // Duplicated call
        assertCode("e3", recordingIterator.next());
        assertCode("e3", replayingIterator.next());
        assertFalse(recordingIterator.hasNext());
        assertFalse(replayingIterator.hasNext());
        assertFalse(replayingIterator.hasNext()); // Duplicated call

        assertEquals(false, mimicRecordingEntitySelector.isContinuous());
        assertEquals(false, mimicReplayingEntitySelector.isContinuous());
        assertEquals(false, mimicRecordingEntitySelector.isNeverEnding());
        assertEquals(false, mimicReplayingEntitySelector.isNeverEnding());
        assertEquals(3L, mimicRecordingEntitySelector.getSize());
        assertEquals(3L, mimicReplayingEntitySelector.getSize());
    }

}
