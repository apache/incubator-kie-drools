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

package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedEntityPlacer;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicRecordingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.composite.CartesianProductMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;

public class QueuedEntityPlacerTest extends AbstractEntityPlacerTest {

    @Test
    public void oneMoveSelector() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
                new TestdataEntity("a"), new TestdataEntity("b"), new TestdataEntity("c"));
        MimicRecordingEntitySelector recordingEntitySelector = new MimicRecordingEntitySelector(
                entitySelector);
        ValueSelector valueSelector = SelectorTestUtils.mockValueSelector(TestdataEntity.class, "value",
                new TestdataValue("1"), new TestdataValue("2"));

        MoveSelector moveSelector = new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                valueSelector,
                false);
        QueuedEntityPlacer placer = new QueuedEntityPlacer(recordingEntitySelector, Collections.singletonList(moveSelector));

        SolverScope solverScope = mock(SolverScope.class);
        placer.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        assertEntityPlacement(placementIterator.next(), "a", "1", "2");
        placer.stepEnded(stepScopeA1);

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        assertEntityPlacement(placementIterator.next(), "b", "1", "2");
        placer.stepEnded(stepScopeA2);

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA3 = mock(AbstractStepScope.class);
        when(stepScopeA3.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA3);
        assertEntityPlacement(placementIterator.next(), "c", "1", "2");
        placer.stepEnded(stepScopeA3);

        assertThat(placementIterator.hasNext()).isFalse();
        placer.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeB);
        placementIterator = placer.iterator();

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        placer.stepStarted(stepScopeB1);
        assertEntityPlacement(placementIterator.next(), "a", "1", "2");
        placer.stepEnded(stepScopeB1);

        placer.phaseEnded(phaseScopeB);

        placer.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 4);
        verifyPhaseLifecycle(valueSelector, 1, 2, 4);
    }

    @Test
    public void multiQueuedMoveSelector() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataMultiVarEntity.class,
                new TestdataMultiVarEntity("a"), new TestdataMultiVarEntity("b"));
        MimicRecordingEntitySelector recordingEntitySelector = new MimicRecordingEntitySelector(
                entitySelector);
        ValueSelector primaryValueSelector = SelectorTestUtils.mockValueSelector(
                TestdataMultiVarEntity.class, "primaryValue",
                new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));
        ValueSelector secondaryValueSelector = SelectorTestUtils.mockValueSelector(
                TestdataMultiVarEntity.class, "secondaryValue",
                new TestdataValue("8"), new TestdataValue("9"));

        List<MoveSelector> moveSelectorList = new ArrayList<>(2);
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                primaryValueSelector,
                false));
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                secondaryValueSelector,
                false));
        QueuedEntityPlacer placer = new QueuedEntityPlacer(recordingEntitySelector, moveSelectorList);

        SolverScope solverScope = mock(SolverScope.class);
        placer.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        assertEntityPlacement(placementIterator.next(), "a", "1", "2", "3");
        placer.stepEnded(stepScopeA1);

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        assertEntityPlacement(placementIterator.next(), "a", "8", "9");
        placer.stepEnded(stepScopeA2);

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA3 = mock(AbstractStepScope.class);
        when(stepScopeA3.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA3);
        assertEntityPlacement(placementIterator.next(), "b", "1", "2", "3");
        placer.stepEnded(stepScopeA3);

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA4 = mock(AbstractStepScope.class);
        when(stepScopeA4.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA4);
        assertEntityPlacement(placementIterator.next(), "b", "8", "9");
        placer.stepEnded(stepScopeA4);

        assertThat(placementIterator.hasNext()).isFalse();
        placer.phaseEnded(phaseScopeA);

        placer.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 4);
        verifyPhaseLifecycle(primaryValueSelector, 1, 1, 4);
        verifyPhaseLifecycle(secondaryValueSelector, 1, 1, 4);
    }

    @Test
    public void cartesianProductMoveSelector() {
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(TestdataMultiVarEntity.class,
                new TestdataMultiVarEntity("a"), new TestdataMultiVarEntity("b"));
        MimicRecordingEntitySelector recordingEntitySelector = new MimicRecordingEntitySelector(
                entitySelector);
        ValueSelector primaryValueSelector = SelectorTestUtils.mockValueSelector(
                TestdataMultiVarEntity.class, "primaryValue",
                new TestdataValue("1"), new TestdataValue("2"), new TestdataValue("3"));
        ValueSelector secondaryValueSelector = SelectorTestUtils.mockValueSelector(
                TestdataMultiVarEntity.class, "secondaryValue",
                new TestdataValue("8"), new TestdataValue("9"));

        List<MoveSelector> moveSelectorList = new ArrayList<>(2);
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                primaryValueSelector,
                false));
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                secondaryValueSelector,
                false));
        MoveSelector moveSelector = new CartesianProductMoveSelector(moveSelectorList, true, false);
        QueuedEntityPlacer placer = new QueuedEntityPlacer(recordingEntitySelector,
                Collections.singletonList(moveSelector));

        SolverScope solverScope = mock(SolverScope.class);
        placer.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        assertAllCodesOfIterator(placementIterator.next().iterator(),
                "a->1+a->8", "a->1+a->9", "a->2+a->8", "a->2+a->9", "a->3+a->8", "a->3+a->9");
        placer.stepEnded(stepScopeA1);

        assertThat(placementIterator.hasNext()).isTrue();
        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        assertAllCodesOfIterator(placementIterator.next().iterator(),
                "b->1+b->8", "b->1+b->9", "b->2+b->8", "b->2+b->9", "b->3+b->8", "b->3+b->9");
        placer.stepEnded(stepScopeA2);

        assertThat(placementIterator.hasNext()).isFalse();
        placer.phaseEnded(phaseScopeA);

        placer.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 2);
        verifyPhaseLifecycle(primaryValueSelector, 1, 1, 2);
        verifyPhaseLifecycle(secondaryValueSelector, 1, 1, 2);
    }

}
