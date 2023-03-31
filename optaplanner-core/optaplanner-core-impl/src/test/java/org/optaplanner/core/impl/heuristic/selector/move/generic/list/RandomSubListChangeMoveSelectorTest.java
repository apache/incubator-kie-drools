package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.heuristic.selector.list.TriangularNumbers.nthTriangle;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.listSize;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockNeverEndingDestinationSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockNeverEndingEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementRef;
import org.optaplanner.core.impl.heuristic.selector.list.RandomSubListSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
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
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;
        int subListCount = 10;
        int destinationSize = 3; // arbitrary

        // The number of subLists of [1, 2, 3, 4] is the 4th triangular number (10).
        assertThat(subListCount).isEqualTo(nthTriangle(listSize(a)) + nthTriangle(listSize(b)));

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                new RandomSubListSelector<>(
                        mockEntitySelector(a, b),
                        mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1),
                        minimumSubListSize,
                        maximumSubListSize),
                mockNeverEndingDestinationSelector(destinationSize, ElementRef.of(b, 0)),
                false);

        TestRandom random = new TestRandom(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1);

        solvingStarted(moveSelector, scoreDirector, random);

        // Every possible subList is selected.
        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * destinationSize,
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

        random.assertIntBoundJustRequested(subListCount);
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
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;
        int subListCount = 10;
        int destinationSize = 13; // arbitrary
        // Selecting reversing moves doubles the total number of selected elements (move selector size).
        int moveSelectorSize = 2 * subListCount * destinationSize;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                new RandomSubListSelector<>(
                        mockEntitySelector(a, b),
                        mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1),
                        minimumSubListSize,
                        maximumSubListSize),
                mockNeverEndingDestinationSelector(destinationSize, ElementRef.of(b, 0)),
                true);

        // Each row is consumed by 1 createUpcomingSelection() call.
        // Columns are: subList index, reversing flag.
        TestRandom random = new TestRandom(
                0, 1, // reversing
                1, 0,
                2, 1, // reversing
                3, 1, // reversing
                4, 0,
                5, 1, // reversing
                6, 0,
                7, 1, // reversing
                8, 1, // reversing
                9, 0,
                -1, -1);

        solvingStarted(moveSelector, scoreDirector, random);

        // Every possible subList is selected; some moves are reversing.
        assertCodesOfNeverEndingMoveSelector(moveSelector, moveSelectorSize,
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
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;
        int maximumSubListSize = 3;
        int subListCount = 5;
        int destinationSize = 51; // arbitrary

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                new RandomSubListSelector<>(
                        mockEntitySelector(a, b),
                        mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1),
                        minimumSubListSize,
                        maximumSubListSize),
                mockNeverEndingDestinationSelector(destinationSize, ElementRef.of(b, 0)),
                false);

        TestRandom random = new TestRandom(0, 1, 2, 3, 4, -1);

        solvingStarted(moveSelector, scoreDirector, random);

        // Only subLists bigger than 1 and smaller than 4 are selected.
        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * destinationSize,
                "|3| {A[0..3]->B[0]}",
                "|3| {A[1..4]->B[0]}",
                "|2| {A[0..2]->B[0]}",
                "|2| {A[1..3]->B[0]}",
                "|2| {A[2..4]->B[0]}");

        random.assertIntBoundJustRequested(subListCount);
    }

    @Test
    void emptyWhenMinimumSubListSizeGreaterThanListSize() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 100;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                new RandomSubListSelector<>(
                        mockEntitySelector(a),
                        mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1),
                        minimumSubListSize,
                        maximumSubListSize),
                mockNeverEndingDestinationSelector(),
                false);

        solvingStarted(moveSelector, scoreDirector);

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
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;
        int maximumSubListSize = 2;
        int subListCount = 2;
        int destinationSize = 13; // arbitrary

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                new RandomSubListSelector<>(
                        mockEntitySelector(a, b, c),
                        mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v4, v1),
                        minimumSubListSize,
                        maximumSubListSize),
                mockNeverEndingDestinationSelector(destinationSize, ElementRef.of(b, 0)),
                false);

        TestRandom random = new TestRandom(0, 1, -1);

        solvingStarted(moveSelector, scoreDirector, random);

        // Only subLists of size 2 are selected.
        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "|2| {A[0..2]->B[0]}",
                "|2| {A[1..3]->B[0]}");

        random.assertIntBoundJustRequested(subListCount);
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
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;
        int subListCount = 9;
        int destinationSize = 25; // arbitrary

        assertThat(subListCount).isEqualTo(nthTriangle(listSize(a)) + nthTriangle(listSize(b)) + nthTriangle(listSize(c)));

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                new RandomSubListSelector<>(
                        mockEntitySelector(a, b, c), // affects subList calculation and the move selector size
                        mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector),
                                v1, v2, v3, v4, v5),
                        minimumSubListSize,
                        maximumSubListSize),
                mockNeverEndingDestinationSelector(destinationSize, ElementRef.of(b, -1)),
                false);

        TestRandom random = new TestRandom(0, 0);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * destinationSize);
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
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 3;
        int maximumSubListSize = 5;
        int subListCount = 16;
        int destinationSize = 7; // arbitrary

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                new RandomSubListSelector<>(
                        mockEntitySelector(a, b, c, d), // affects subList calculation and the move selector size
                        mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1),
                        minimumSubListSize,
                        maximumSubListSize),
                mockNeverEndingDestinationSelector(destinationSize, ElementRef.of(b, 0)),
                false);

        TestRandom random = new TestRandom(0);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, subListCount * destinationSize);
    }

    @Test
    void phaseLifecycle() {
        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector();
        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockNeverEndingEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector));
        DestinationSelector<TestdataListSolution> destinationSelector = mockNeverEndingDestinationSelector();

        RandomSubListChangeMoveSelector<TestdataListSolution> moveSelector = new RandomSubListChangeMoveSelector<>(
                new RandomSubListSelector<>(
                        entitySelector,
                        valueSelector,
                        minimumSubListSize,
                        maximumSubListSize),
                destinationSelector,
                false);

        SolverScope<TestdataListSolution> solverScope = solvingStarted(moveSelector, scoreDirector);
        AbstractPhaseScope<TestdataListSolution> phaseScope = phaseStarted(moveSelector, solverScope);

        AbstractStepScope<TestdataListSolution> stepScope1 = stepStarted(moveSelector, phaseScope);
        moveSelector.stepEnded(stepScope1);

        AbstractStepScope<TestdataListSolution> stepScope2 = stepStarted(moveSelector, phaseScope);
        moveSelector.stepEnded(stepScope2);

        moveSelector.phaseEnded(phaseScope);
        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 2);
        verifyPhaseLifecycle(valueSelector, 1, 1, 2);
        verifyPhaseLifecycle(destinationSelector, 1, 1, 2);
    }
}
