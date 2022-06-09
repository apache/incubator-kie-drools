package org.optaplanner.core.impl.heuristic.selector.move.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.DO_NOT_ASSERT_SIZE;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicRecordingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;

class CartesianProductMoveSelectorTest {

    @Test
    void originSelectionNotIgnoringEmpty() {
        originSelection(false);
    }

    @Test
    void originSelectionIgnoringEmpty() {
        originSelection(true);
    }

    public void originSelection(boolean ignoreEmptyChildIterators) {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList,
                ignoreEmptyChildIterators, false);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertAllCodesOfMoveSelector(moveSelector,
                "a1+b1", "a1+b2",
                "a2+b1", "a2+b2",
                "a3+b1", "a3+b2");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void emptyFirstOriginSelectionNotIgnoringEmpty() {
        emptyOriginSelection(false, true, false);
    }

    @Test
    void emptyFirstOriginSelectionIgnoringEmpty() {
        emptyOriginSelection(true, true, false);
    }

    @Test
    void emptySecondOriginSelectionNotIgnoringEmpty() {
        emptyOriginSelection(false, false, true);
    }

    @Test
    void emptySecondOriginSelectionIgnoringEmpty() {
        emptyOriginSelection(true, false, true);
    }

    @Test
    void emptyAllOriginSelectionNotIgnoringEmpty() {
        emptyOriginSelection(false, true, true);
    }

    @Test
    void emptyAllOriginSelectionIgnoringEmpty() {
        emptyOriginSelection(true, true, true);
    }

    public void emptyOriginSelection(boolean ignoreEmptyChildIterators, boolean emptyFirst, boolean emptySecond) {
        assertThat(emptyFirst || emptySecond).isTrue();
        MoveSelector nonEmptyChildMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3")); // One side is not empty
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(emptyFirst
                ? SelectorTestUtils.mockMoveSelector(DummyMove.class)
                : nonEmptyChildMoveSelector);
        childMoveSelectorList.add(emptySecond
                ? SelectorTestUtils.mockMoveSelector(DummyMove.class)
                : nonEmptyChildMoveSelector);
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList,
                ignoreEmptyChildIterators, false);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        if (ignoreEmptyChildIterators && !(emptyFirst && emptySecond)) {
            assertAllCodesOfMoveSelector(moveSelector, "a1", "a2", "a3");
        } else {
            assertAllCodesOfMoveSelector(moveSelector);
        }

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void originSelection3ChildMoveSelectorsNotIgnoringEmpty() {
        originSelection3ChildMoveSelectors(false);
    }

    @Test
    void originSelection3ChildMoveSelectorsIgnoringEmpty() {
        originSelection3ChildMoveSelectors(true);
    }

    public void originSelection3ChildMoveSelectors(boolean ignoreEmptyChildIterators) {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("c1"), new DummyMove("c2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList,
                ignoreEmptyChildIterators, false);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertAllCodesOfMoveSelector(moveSelector,
                "a1+b1+c1", "a1+b1+c2", "a1+b2+c1", "a1+b2+c2",
                "a2+b1+c1", "a2+b1+c2", "a2+b2+c1", "a2+b2+c2");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void emptyOriginSelection3ChildMoveSelectorsNotIgnoringEmpty() {
        emptyOriginSelection3ChildMoveSelectors(false);
    }

    @Test
    void emptyOriginSelection3ChildMoveSelectorsIgnoringEmpty() {
        emptyOriginSelection3ChildMoveSelectors(true);
    }

    public void emptyOriginSelection3ChildMoveSelectors(boolean ignoreEmptyChildIterators) {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("c1"), new DummyMove("c2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList,
                ignoreEmptyChildIterators, false);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        if (ignoreEmptyChildIterators) {
            assertAllCodesOfMoveSelector(moveSelector,
                    "a1+c1", "a1+c2", "a2+c1", "a2+c2");
        } else {
            assertAllCodesOfMoveSelector(moveSelector);
        }

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void classicRandomSelectionNotIgnoringEmpty() {
        classicRandomSelection(false);
    }

    @Test
    void classicRandomSelectionIgnoringEmpty() {
        classicRandomSelection(true);
    }

    public void classicRandomSelection(boolean ignoreEmptyChildIterators) {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList,
                ignoreEmptyChildIterators, true);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertCodesOfNeverEndingMoveSelector(moveSelector, 6, "a1+b1", "a2+b2", "a3+b1", "a1+b2", "a2+b1", "a3+b2");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void emptyRandomSelectionNotIgnoringEmpty() {
        emptyRandomSelection(false);
    }

    @Test
    void emptyRandomSelectionIgnoringEmpty() {
        emptyRandomSelection(true);
    }

    public void emptyRandomSelection(boolean ignoreEmptyChildIterators) {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2"))); // One side is not empty
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList,
                ignoreEmptyChildIterators, true);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        if (ignoreEmptyChildIterators) {
            assertCodesOfNeverEndingMoveSelector(moveSelector, 2L, "b1", "b2");
        } else {
            assertEmptyNeverEndingMoveSelector(moveSelector);
        }

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void randomSelection3ChildMoveSelectorsNotIgnoringEmpty() {
        randomSelection3ChildMoveSelectors(false);
    }

    @Test
    void randomSelection3ChildMoveSelectorsIgnoringEmpty() {
        randomSelection3ChildMoveSelectors(true);
    }

    public void randomSelection3ChildMoveSelectors(boolean ignoreEmptyChildIterators) {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("c1"), new DummyMove("c2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList,
                ignoreEmptyChildIterators, true);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertCodesOfNeverEndingMoveSelector(moveSelector, 8L, "a1+b1+c1", "a2+b2+c2", "a1+b1+c1");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    void emptyRandomSelection3ChildMoveSelectorsNotIgnoringEmpty() {
        emptyRandomSelection3ChildMoveSelectors(false);
    }

    @Test
    void emptyRandomSelection3ChildMoveSelectorsIgnoringEmpty() {
        emptyRandomSelection3ChildMoveSelectors(true);
    }

    public void emptyRandomSelection3ChildMoveSelectors(boolean ignoreEmptyChildIterators) {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("c1"), new DummyMove("c2"), new DummyMove("c3")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList,
                ignoreEmptyChildIterators, true);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        if (ignoreEmptyChildIterators) {
            assertCodesOfNeverEndingMoveSelector(moveSelector, 6L, "b1+c1", "b2+c2", "b1+c3");
        } else {
            assertEmptyNeverEndingMoveSelector(moveSelector);
        }

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifyPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    // ************************************************************************
    // Integration with mimic
    // ************************************************************************

    @Test
    void originalMimicNotIgnoringEmpty() {
        originalMimic(false);
    }

    @Test
    void originalMimicIgnoringEmpty() {
        originalMimic(true);
    }

    public void originalMimic(boolean ignoreEmptyChildIterators) {
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
                recordingEntitySelector,
                primaryValueSelector,
                false));
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                secondaryValueSelector,
                false));
        MoveSelector moveSelector = new CartesianProductMoveSelector(moveSelectorList,
                ignoreEmptyChildIterators, false);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        assertAllCodesOfMoveSelector(moveSelector, DO_NOT_ASSERT_SIZE,
                "a->1+a->8", "a->1+a->9", "a->2+a->8", "a->2+a->9", "a->3+a->8", "a->3+a->9",
                "b->1+b->8", "b->1+b->9", "b->2+b->8", "b->2+b->9", "b->3+b->8", "b->3+b->9");
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        assertAllCodesOfMoveSelector(moveSelector, DO_NOT_ASSERT_SIZE,
                "a->1+a->8", "a->1+a->9", "a->2+a->8", "a->2+a->9", "a->3+a->8", "a->3+a->9",
                "b->1+b->8", "b->1+b->9", "b->2+b->8", "b->2+b->9", "b->3+b->8", "b->3+b->9");
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 2);
        verifyPhaseLifecycle(primaryValueSelector, 1, 1, 2);
        verifyPhaseLifecycle(secondaryValueSelector, 1, 1, 2);
    }

    @Test
    void randomMimicNotIgnoringEmpty() {
        randomMimic(false);
    }

    @Test
    void randomMimicIgnoringEmpty() {
        randomMimic(true);
    }

    public void randomMimic(boolean ignoreEmptyChildIterators) {
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
                recordingEntitySelector,
                primaryValueSelector,
                false));
        moveSelectorList.add(new ChangeMoveSelector(
                new MimicReplayingEntitySelector(recordingEntitySelector),
                secondaryValueSelector,
                false));
        MoveSelector moveSelector = new CartesianProductMoveSelector(moveSelectorList,
                ignoreEmptyChildIterators, true);

        SolverScope solverScope = mock(SolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        assertCodesOfNeverEndingMoveSelector(moveSelector, 24L,
                "a->1+a->8", "a->2+a->9", "a->3+a->8", "b->1+a->9", "b->2+b->8", "b->3+b->9");
        moveSelector.stepEnded(stepScopeA1);

        moveSelector.phaseEnded(phaseScopeA);

        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 1);
        verifyPhaseLifecycle(primaryValueSelector, 1, 1, 1);
        verifyPhaseLifecycle(secondaryValueSelector, 1, 1, 1);
    }

}
