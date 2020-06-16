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

package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;

public class MimicReplayingEntitySelectorTest {

    @Test
    public void originalSelection() {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"));

        MimicRecordingEntitySelector recordingEntitySelector = new MimicRecordingEntitySelector(childEntitySelector);
        MimicReplayingEntitySelector replayingEntitySelector = new MimicReplayingEntitySelector(recordingEntitySelector);

        SolverScope solverScope = mock(SolverScope.class);
        recordingEntitySelector.solvingStarted(solverScope);
        replayingEntitySelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
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

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
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

        verifyPhaseLifecycle(childEntitySelector, 1, 2, 3);
        verify(childEntitySelector, times(3)).iterator();
    }

    private void runOriginalAsserts(MimicRecordingEntitySelector recordingEntitySelector,
            MimicReplayingEntitySelector replayingEntitySelector) {
        Iterator<Object> recordingIterator = recordingEntitySelector.iterator();
        assertThat(recordingIterator).isNotNull();
        Iterator<Object> replayingIterator = replayingEntitySelector.iterator();
        assertThat(replayingIterator).isNotNull();

        assertThat(recordingIterator.hasNext()).isTrue();
        assertThat(replayingIterator.hasNext()).isTrue();
        assertCode("e1", recordingIterator.next());
        assertCode("e1", replayingIterator.next());
        assertThat(recordingIterator.hasNext()).isTrue();
        assertThat(replayingIterator.hasNext()).isTrue();
        assertCode("e2", recordingIterator.next());
        assertCode("e2", replayingIterator.next());
        // Extra call
        assertThat(replayingIterator.hasNext()).isFalse();
        assertThat(recordingIterator.hasNext()).isTrue();
        assertThat(replayingIterator.hasNext()).isTrue();
        // Duplicated call
        assertThat(replayingIterator.hasNext()).isTrue();
        assertCode("e3", recordingIterator.next());
        assertCode("e3", replayingIterator.next());
        assertThat(recordingIterator.hasNext()).isFalse();
        assertThat(replayingIterator.hasNext()).isFalse();
        // Duplicated call
        assertThat(replayingIterator.hasNext()).isFalse();

        assertThat(recordingEntitySelector.isCountable()).isTrue();
        assertThat(replayingEntitySelector.isCountable()).isTrue();
        assertThat(recordingEntitySelector.isNeverEnding()).isFalse();
        assertThat(replayingEntitySelector.isNeverEnding()).isFalse();
        assertThat(recordingEntitySelector.getSize()).isEqualTo(3L);
        assertThat(replayingEntitySelector.getSize()).isEqualTo(3L);
    }

}
