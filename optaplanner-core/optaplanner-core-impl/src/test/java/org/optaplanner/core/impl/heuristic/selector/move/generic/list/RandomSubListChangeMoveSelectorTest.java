package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.heuristic.selector.move.generic.list.TriangularNumbers.nthTriangle;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.optaplanner.core.impl.testutil.TestRandom;

class RandomSubListChangeMoveSelectorTest {

    @Test
    void randomUnrestricted() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b),
                // The value selector is longer than the number of expected codes because it is expected
                // to be never ending, so it must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v1, v1, v1, v1, v1, v1, v1, v1, v1, v1, v1),
                minimumSubListSize,
                maximumSubListSize,
                false);

        final int destinationIndexRange = 6; // value count + entity count
        final int b0 = destinationIndexRange - 1; // the last position
        // Alternating subList and destination indexes.
        TestRandom random = new TestRandom(0, b0, 1, b0, 2, b0, 3, b0, 4, b0, 5, b0, 6, b0, 7, b0, 8, b0, 9, b0, 99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        // Every possible subList is selected.
        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "|4| {A[0..4]->B[0]}",
                "|3| {A[0..3]->B[0]}",
                "|3| {A[1..4]->B[0]}",
                "|2| {A[0..2]->B[0]}",
                "|2| {A[1..3]->B[0]}",
                "|2| {A[2..4]->B[0]}",
                "|1| {A[0..1]->B[0]}",
                "|1| {A[1..2]->B[0]}",
                "|1| {A[2..3]->B[0]}",
                "|1| {A[3..4]->B[0]}");

        random.assertIntBoundJustRequested(destinationIndexRange);
    }

    @Test
    void randomReversing() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b),
                // The value selector is longer than the number of expected codes because it is expected
                // to be never ending, so it must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v1, v1, v1, v1, v1, v1, v1, v1, v1, v1, v1),
                minimumSubListSize,
                maximumSubListSize,
                true);

        final int destinationIndexRange = 6; // value count + entity count
        final int b0 = destinationIndexRange - 1; // the last position
        // Each row is consumed by 1 createUpcomingSelection() call.
        // Columns are: subList index, destination index, reversing flag.
        TestRandom random = new TestRandom(
                0, b0, 1, // reversing
                1, b0, 0,
                2, b0, 1, // reversing
                3, b0, 1, // reversing
                4, b0, 0,
                5, b0, 1, // reversing
                6, b0, 0,
                7, b0, 1, // reversing
                8, b0, 1, // reversing
                9, b0, 0,
                99, 99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        // Every possible subList is selected.
        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "|4| {A[0..4]-reversing->B[0]}",
                "|3| {A[0..3]->B[0]}",
                "|3| {A[1..4]-reversing->B[0]}",
                "|2| {A[0..2]-reversing->B[0]}",
                "|2| {A[1..3]->B[0]}",
                "|2| {A[2..4]-reversing->B[0]}",
                "|1| {A[0..1]->B[0]}",
                "|1| {A[1..2]-reversing->B[0]}",
                "|1| {A[2..3]-reversing->B[0]}",
                "|1| {A[3..4]->B[0]}");
    }

    @Test
    void randomWithSubListSizeBounds() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;
        int maximumSubListSize = 3;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b),
                // The value selector is longer than the number of expected codes because it is expected
                // to be never ending, so it must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v1, v1, v1, v1, v1, v1, v1, v1, v1, v1, v1),
                minimumSubListSize,
                maximumSubListSize,
                false);

        final int destinationIndexRange = 6; // value count + entity count
        final int b0 = destinationIndexRange - 1; // the last position

        // Alternating subList and destination indexes.
        TestRandom random = new TestRandom(0, b0, 1, b0, 2, b0, 3, b0, 4, b0, 99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        // Every possible subList is selected.
        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "|3| {A[0..3]->B[0]}",
                "|3| {A[1..4]->B[0]}",
                "|2| {A[0..2]->B[0]}",
                "|2| {A[1..3]->B[0]}",
                "|2| {A[2..4]->B[0]}");

        random.assertIntBoundJustRequested(destinationIndexRange);
    }

    @Test
    void emptyWhenMinimumSubListSizeGreaterThanListSize() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 100;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a),
                // The value selector is longer than the number of expected codes because it is expected
                // to be never ending, so it must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v1, v1, v1),
                minimumSubListSize,
                maximumSubListSize,
                false);

        TestRandom random = new TestRandom(new int[] {});

        solvingStarted(moveSelector, scoreDirector, random);

        assertEmptyNeverEndingMoveSelector(moveSelector);
    }

    @Test
    void skipSubListsSmallerThanMinimumSize() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v4);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;
        int maximumSubListSize = 2;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b, c),
                // The value selector is longer than the number of expected codes because it is expected
                // to be never ending, so it must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v4, v1, v4, v1, v4, v1, v4),
                minimumSubListSize,
                maximumSubListSize,
                false);

        final int destinationIndexRange = 7; // value count + entity count
        final int b0 = 4;

        // Alternating subList and destination indexes.
        TestRandom random = new TestRandom(0, b0, 1, b0, 2, b0, 3, b0, 4, b0, 99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        // Every possible subList is selected.
        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "|2| {A[0..2]->B[0]}",
                "|2| {A[1..3]->B[0]}");

        random.assertIntBoundJustRequested(destinationIndexRange);
    }

    @Test
    void sizeUnrestricted() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v4, v5);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int subListCount = 9;
        assertThat(subListCount).isEqualTo(nthTriangle(listSize(a)) + nthTriangle(listSize(b)) + nthTriangle(listSize(c)));
        int destinationIndexRange = 8; // value count + entity count

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                // Selectors must be accurate; their sizes affect the moveSelector size.
                mockEntitySelector(a, b, c),
                mockEntityIndependentValueSelector(v1, v2, v3, v4, v5),
                minimumSubListSize,
                maximumSubListSize,
                false);

        TestRandom random = new TestRandom(0, 0);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * destinationIndexRange);
    }

    static int listSize(TestdataListEntity entity) {
        return entity.getValueList().size();
    }

    @Test
    void sizeWithBounds() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListValue v6 = new TestdataListValue("6");
        TestdataListValue v7 = new TestdataListValue("7");
        TestdataListValue v11 = new TestdataListValue("11");
        TestdataListValue v12 = new TestdataListValue("12");
        TestdataListValue v13 = new TestdataListValue("13");
        TestdataListValue v21 = new TestdataListValue("21");
        TestdataListValue v22 = new TestdataListValue("22");
        TestdataListValue v23 = new TestdataListValue("23");
        TestdataListValue v24 = new TestdataListValue("24");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4, v5, v6, v7);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v11, v12, v13);
        TestdataListEntity d = TestdataListEntity.createWithValues("D", v21, v22, v23, v24);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 3;
        int maximumSubListSize = 5;
        int subListCount = 16;
        int destinationIndexRange = 18; // value count + entity count

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                // Selectors must be accurate; their sizes affect the moveSelector size.
                mockEntitySelector(a, b, c, d),
                mockEntityIndependentValueSelector(v1, v2, v3, v4, v5, v6, v7, v11, v12, v13, v21, v22, v23, v24),
                minimumSubListSize,
                maximumSubListSize,
                false);

        TestRandom random = new TestRandom(0, 0);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * destinationIndexRange);
    }

    @Test
    void phaseLifecycle() {
        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector();
        EntityIndependentValueSelector<TestdataListSolution> valueSelector = mockEntityIndependentValueSelector();
        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                entitySelector,
                valueSelector,
                minimumSubListSize,
                maximumSubListSize,
                false);

        TestRandom random = new TestRandom(new int[] {});

        SolverScope<TestdataListSolution> solverScope = solvingStarted(moveSelector, scoreDirector, random);
        AbstractPhaseScope<TestdataListSolution> phaseScope = phaseStarted(moveSelector, solverScope);

        AbstractStepScope<TestdataListSolution> stepScope1 = stepStarted(moveSelector, phaseScope);
        moveSelector.stepEnded(stepScope1);

        AbstractStepScope<TestdataListSolution> stepScope2 = stepStarted(moveSelector, phaseScope);
        moveSelector.stepEnded(stepScope2);

        moveSelector.phaseEnded(phaseScope);
        moveSelector.solvingEnded(solverScope);

        // The invocation counts are doubled because both the move selector and its nested subList selector have
        // their own phaseLifecycleSupport and both register the given entitySelector and valueSelector.
        // TODO is it OK for the move selector and subList selector to share the same entitySelector and valueSelector instances?
        verifyPhaseLifecycle(entitySelector, 2, 2, 4);
        verifyPhaseLifecycle(valueSelector, 2, 2, 4);
    }
}
