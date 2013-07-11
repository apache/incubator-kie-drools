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

package org.optaplanner.core.impl.heuristic.selector.entity.replay;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.FromSolutionEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.FilteringEntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class ReplayingEntitySelectorTest {

    @Test
    public void originalSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"));

        RecordingEntitySelector recordingEntitySelector = new RecordingEntitySelector(childEntitySelector);
        ReplayingEntitySelector replayingEntitySelector = new ReplayingEntitySelector(recordingEntitySelector);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        recordingEntitySelector.solvingStarted(solverScope);
        replayingEntitySelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        recordingEntitySelector.phaseStarted(phaseScopeA);
        replayingEntitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        recordingEntitySelector.stepStarted(stepScopeA1);
        replayingEntitySelector.stepStarted(stepScopeA1);
        runOriginalAsserts(recordingEntitySelector, replayingEntitySelector);
        recordingEntitySelector.stepEnded(stepScopeA1);
        replayingEntitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        recordingEntitySelector.stepStarted(stepScopeA2);
        replayingEntitySelector.stepStarted(stepScopeA2);
        runOriginalAsserts(recordingEntitySelector, replayingEntitySelector);
        recordingEntitySelector.stepEnded(stepScopeA2);
        replayingEntitySelector.stepEnded(stepScopeA2);

        recordingEntitySelector.phaseEnded(phaseScopeA);
        replayingEntitySelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        recordingEntitySelector.phaseStarted(phaseScopeB);
        replayingEntitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        recordingEntitySelector.stepStarted(stepScopeB1);
        replayingEntitySelector.stepStarted(stepScopeB1);
        runOriginalAsserts(recordingEntitySelector, replayingEntitySelector);
        recordingEntitySelector.stepEnded(stepScopeB1);
        replayingEntitySelector.stepEnded(stepScopeB1);

        recordingEntitySelector.phaseEnded(phaseScopeB);
        replayingEntitySelector.phaseEnded(phaseScopeB);

        recordingEntitySelector.solvingEnded(solverScope);
        replayingEntitySelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childEntitySelector, 1, 2, 3);
        verify(childEntitySelector, times(3)).iterator();
    }

    private void runOriginalAsserts(
            RecordingEntitySelector recordingEntitySelector, ReplayingEntitySelector replayingEntitySelector) {
        Iterator<Object> recordingIterator = recordingEntitySelector.iterator();
        assertNotNull(recordingIterator);
        Iterator<Object> replayingIterator = replayingEntitySelector.iterator();
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

        assertEquals(false, recordingEntitySelector.isContinuous());
        assertEquals(false, replayingEntitySelector.isContinuous());
        assertEquals(false, recordingEntitySelector.isNeverEnding());
        assertEquals(false, replayingEntitySelector.isNeverEnding());
        assertEquals(3L, recordingEntitySelector.getSize());
        assertEquals(3L, replayingEntitySelector.getSize());
    }

}
