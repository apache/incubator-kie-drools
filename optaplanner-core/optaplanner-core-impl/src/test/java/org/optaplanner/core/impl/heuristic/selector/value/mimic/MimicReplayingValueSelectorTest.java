package org.optaplanner.core.impl.heuristic.selector.value.mimic;

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
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

class MimicReplayingValueSelectorTest {

    @Test
    void originalSelection() {
        EntityIndependentValueSelector childValueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                TestdataEntity.class, "value",
                new TestdataValue("v1"), new TestdataValue("v2"), new TestdataValue("v3"));

        MimicRecordingValueSelector recordingValueSelector = new MimicRecordingValueSelector(childValueSelector);
        MimicReplayingValueSelector replayingValueSelector = new MimicReplayingValueSelector(recordingValueSelector);

        SolverScope solverScope = mock(SolverScope.class);
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
        assertThat(recordingIterator).isNotNull();
        Iterator<Object> replayingIterator = replayingValueSelector.iterator();
        assertThat(replayingIterator).isNotNull();

        assertThat(recordingIterator.hasNext()).isTrue();
        assertThat(replayingIterator.hasNext()).isTrue();
        assertCode("v1", recordingIterator.next());
        assertCode("v1", replayingIterator.next());
        assertThat(recordingIterator.hasNext()).isTrue();
        assertThat(replayingIterator.hasNext()).isTrue();
        assertCode("v2", recordingIterator.next());
        assertCode("v2", replayingIterator.next());
        // Extra call
        assertThat(replayingIterator.hasNext()).isFalse();
        assertThat(recordingIterator.hasNext()).isTrue();
        assertThat(replayingIterator.hasNext()).isTrue();
        // Duplicated call
        assertThat(replayingIterator.hasNext()).isTrue();
        assertCode("v3", recordingIterator.next());
        assertCode("v3", replayingIterator.next());
        assertThat(recordingIterator.hasNext()).isFalse();
        assertThat(replayingIterator.hasNext()).isFalse();
        // Duplicated call
        assertThat(replayingIterator.hasNext()).isFalse();

        assertThat(recordingValueSelector.isCountable()).isTrue();
        assertThat(replayingValueSelector.isCountable()).isTrue();
        assertThat(recordingValueSelector.isNeverEnding()).isFalse();
        assertThat(replayingValueSelector.isNeverEnding()).isFalse();
        assertThat(recordingValueSelector.getSize()).isEqualTo(3L);
        assertThat(replayingValueSelector.getSize()).isEqualTo(3L);
    }

}
