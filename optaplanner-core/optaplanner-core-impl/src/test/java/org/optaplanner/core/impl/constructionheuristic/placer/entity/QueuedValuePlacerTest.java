package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.constructionheuristic.placer.entity.PlacementAssertions.assertValuePlacement;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedValuePlacer;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicRecordingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

class QueuedValuePlacerTest {

    @Test
    void oneMoveSelector() {
        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector<TestdataSolution> entitySelector =
                SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                        new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"));
        EntityIndependentValueSelector<TestdataSolution> valueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                variableDescriptor,
                new TestdataValue("1"), new TestdataValue("2"));
        MimicRecordingValueSelector<TestdataSolution> recordingValueSelector =
                new MimicRecordingValueSelector<>(valueSelector);

        MoveSelector<TestdataSolution> moveSelector = new ChangeMoveSelector<>(entitySelector,
                new MimicReplayingValueSelector<>(recordingValueSelector), false);
        QueuedValuePlacer<TestdataSolution> placer = new QueuedValuePlacer<>(recordingValueSelector, moveSelector);

        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        placer.solvingStarted(solverScope);

        AbstractPhaseScope<TestdataSolution> phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement<TestdataSolution>> placementIterator = placer.iterator();

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope<TestdataSolution> stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        assertValuePlacement(placementIterator.next(), "1", "a", "b", "c");
        placer.stepEnded(stepScopeA1);

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope<TestdataSolution> stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        assertValuePlacement(placementIterator.next(), "2", "a", "b", "c");
        placer.stepEnded(stepScopeA2);

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope<TestdataSolution> stepScopeA3 = mock(AbstractStepScope.class);
        when(stepScopeA3.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA3);
        assertValuePlacement(placementIterator.next(), "1", "a", "b", "c");
        placer.stepEnded(stepScopeA3);

        // Requires adding ReinitializeVariableValueSelector complexity to work
        // assertFalse(placementIterator.hasNext());
        placer.phaseEnded(phaseScopeA);

        AbstractPhaseScope<TestdataSolution> phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeB);
        placementIterator = placer.iterator();

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope<TestdataSolution> stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        placer.stepStarted(stepScopeB1);
        assertValuePlacement(placementIterator.next(), "1", "a", "b", "c");
        placer.stepEnded(stepScopeB1);

        placer.phaseEnded(phaseScopeB);

        placer.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 4);
        verifyPhaseLifecycle(valueSelector, 1, 2, 4);
    }

}
