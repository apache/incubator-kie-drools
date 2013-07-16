package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.impl.constructionheuristic.placer.Placement;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedEntityPlacer;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicRecordingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.composite.CartesianProductMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.move.CompositeMove;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class QueuedEntityPlacerTest {

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

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        placer.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        assertPlacement(placementIterator.next(), "a", "1", "2");
        placer.stepEnded(stepScopeA1);

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        assertPlacement(placementIterator.next(), "b", "1", "2");
        placer.stepEnded(stepScopeA2);

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA3 = mock(AbstractStepScope.class);
        when(stepScopeA3.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA3);
        assertPlacement(placementIterator.next(), "c", "1", "2");
        placer.stepEnded(stepScopeA3);

        assertFalse(placementIterator.hasNext());
        placer.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeB);
        placementIterator = placer.iterator();

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        placer.stepStarted(stepScopeB1);
        assertPlacement(placementIterator.next(), "a", "1", "2");
        placer.stepEnded(stepScopeB1);

        placer.phaseEnded(phaseScopeB);

        placer.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(entitySelector, 1, 2, 4);
        verifySolverPhaseLifecycle(valueSelector, 1, 2, 4);
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

        List<MoveSelector> moveSelectorList = new ArrayList<MoveSelector>(2);
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                primaryValueSelector,
                false));
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                secondaryValueSelector,
                false));
        QueuedEntityPlacer placer = new QueuedEntityPlacer(recordingEntitySelector, moveSelectorList);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        placer.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        assertPlacement(placementIterator.next(), "a", "1", "2", "3");
        placer.stepEnded(stepScopeA1);

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        assertPlacement(placementIterator.next(), "a", "8", "9");
        placer.stepEnded(stepScopeA2);

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA3 = mock(AbstractStepScope.class);
        when(stepScopeA3.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA3);
        assertPlacement(placementIterator.next(), "b", "1", "2", "3");
        placer.stepEnded(stepScopeA3);

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA4 = mock(AbstractStepScope.class);
        when(stepScopeA4.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA4);
        assertPlacement(placementIterator.next(), "b", "8", "9");
        placer.stepEnded(stepScopeA4);

        assertFalse(placementIterator.hasNext());
        placer.phaseEnded(phaseScopeA);

        placer.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(entitySelector, 1, 1, 4);
        verifySolverPhaseLifecycle(primaryValueSelector, 1, 1, 4);
        verifySolverPhaseLifecycle(secondaryValueSelector, 1, 1, 4);
    }

    private void assertPlacement(Placement placement, String entityCode, String... valueCodes) {
        Iterator<Move> iterator = placement.iterator();
        assertNotNull(iterator);
        for (String valueCode : valueCodes) {
            assertTrue(iterator.hasNext());
            ChangeMove move = (ChangeMove) iterator.next();
            assertCode(entityCode, move.getEntity());
            assertCode(valueCode, move.getToPlanningValue());
        }
        assertFalse(iterator.hasNext());
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

        List<MoveSelector> moveSelectorList = new ArrayList<MoveSelector>(2);
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                primaryValueSelector,
                false));
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                secondaryValueSelector,
                false));
        MoveSelector moveSelector = new CartesianProductMoveSelector(moveSelectorList, false);
        QueuedEntityPlacer placer = new QueuedEntityPlacer(recordingEntitySelector,
                Collections.singletonList(moveSelector));

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        placer.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        placer.phaseStarted(phaseScopeA);
        Iterator<Placement> placementIterator = placer.iterator();

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA1);
        assertCartesianProductPlacement(placementIterator.next(), "a", new String[][]{
                {"1", "8"}, {"1", "9"},
                {"2", "8"}, {"2", "9"},
                {"3", "8"}, {"3", "9"}});
        placer.stepEnded(stepScopeA1);

        assertTrue(placementIterator.hasNext());
        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        placer.stepStarted(stepScopeA2);
        assertCartesianProductPlacement(placementIterator.next(), "b", new String[][]{
                {"1", "8"}, {"1", "9"},
                {"2", "8"}, {"2", "9"},
                {"3", "8"}, {"3", "9"}});
        placer.stepEnded(stepScopeA2);

        assertFalse(placementIterator.hasNext());
        placer.phaseEnded(phaseScopeA);

        placer.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(entitySelector, 1, 1, 2);
        verifySolverPhaseLifecycle(primaryValueSelector, 1, 1, 2);
        verifySolverPhaseLifecycle(secondaryValueSelector, 1, 1, 2);
    }

    private void assertCartesianProductPlacement(Placement placement, String entityCode,
            String[][] valueCodeCombinations) {
        Iterator<Move> iterator = placement.iterator();
        assertNotNull(iterator);
        for (String[] valueCodeCombination : valueCodeCombinations) {
            assertTrue(iterator.hasNext());
            CompositeMove move = (CompositeMove) iterator.next();
            List<Move> subMoveList = move.getMoveList();
            assertEquals(valueCodeCombination.length, subMoveList.size());
            for (int i = 0; i < valueCodeCombination.length; i++) {
                ChangeMove changeMove = (ChangeMove) subMoveList.get(i);
                assertCode(entityCode, changeMove.getEntity());
                assertCode(valueCodeCombination[i], changeMove.getToPlanningValue());
            }
        }
        assertFalse(iterator.hasNext());
    }

}
