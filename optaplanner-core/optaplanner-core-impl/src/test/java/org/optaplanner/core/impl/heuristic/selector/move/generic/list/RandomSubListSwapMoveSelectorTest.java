package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.stream.Stream;

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

class RandomSubListSwapMoveSelectorTest {

    @Test
    void sameEntityUnrestricted() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        // The value selector is longer than the number of expected codes because it is expected
        // to be never ending, so it must not be exhausted after the last asserted code.
        int valueSelectorSize = 20;
        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a),
                mockEntityIndependentValueSelector(Stream.generate(() -> v1).limit(valueSelectorSize).toArray()),
                mockEntityIndependentValueSelector(Stream.generate(() -> v1).limit(valueSelectorSize).toArray()),
                minimumSubListSize,
                maximumSubListSize,
                false);

        // Alternating left and right subList indexes.
        //      L, R
        TestRandom random = new TestRandom(
                0, 0,
                0, 1,
                0, 2,
                0, 3,
                0, 4,
                0, 5,
                0, 6,
                0, 7,
                0, 8,
                0, 9,
                1, 8,
                2, 7,
                3, 6,
                4, 5,
                5, 4,
                6, 3,
                7, 2,
                8, 1,
                9, 0,
                99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "{A[0+4]} <-> {A[0+4]}",
                "{A[0+4]} <-> {A[0+3]}",
                "{A[0+4]} <-> {A[1+3]}",
                "{A[0+4]} <-> {A[0+2]}",
                "{A[0+4]} <-> {A[1+2]}",
                "{A[0+4]} <-> {A[2+2]}",
                "{A[0+4]} <-> {A[0+1]}",
                "{A[0+4]} <-> {A[1+1]}",
                "{A[0+4]} <-> {A[2+1]}",
                "{A[0+4]} <-> {A[3+1]}",
                "{A[0+3]} <-> {A[2+1]}",
                "{A[1+3]} <-> {A[1+1]}",
                "{A[0+2]} <-> {A[0+1]}",
                "{A[1+2]} <-> {A[2+2]}",
                "{A[1+2]} <-> {A[2+2]}", // equivalent to {A[2+2]} <-> {A[1+2]}
                "{A[0+1]} <-> {A[0+2]}",
                "{A[1+1]} <-> {A[1+3]}",
                "{A[0+3]} <-> {A[2+1]}", // equivalent to {A[2+1]} <-> {A[0+3]}
                "{A[0+4]} <-> {A[3+1]}"); // equivalent to {A[3+1]} <-> {A[0+4]}

        random.assertIntBoundJustRequested(10);
    }

    @Test
    void reversing() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListValue v6 = new TestdataListValue("6");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);
        TestdataListEntity b = TestdataListEntity.createWithValues("B", v5, v6);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        // The value selector is longer than the number of expected codes because it is expected
        // to be never ending, so it must not be exhausted after the last asserted code.
        int valueSelectorSize = 7;
        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b),
                mockEntityIndependentValueSelector(Stream.generate(() -> v1).limit(valueSelectorSize).toArray()),
                mockEntityIndependentValueSelector(Stream.generate(() -> v5).limit(valueSelectorSize).toArray()),
                minimumSubListSize,
                maximumSubListSize,
                true);

        // Each row is consumed by 1 createUpcomingSelection() call.
        // Columns are: left subList index, right subList index, reversing flag.
        TestRandom random = new TestRandom(
                0, 2, 1,
                0, 1, 0,
                0, 0, 1,
                1, 0, 0,
                2, 0, 1,
                3, 0, 1,
                99, 99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "{A[0+3]} <-reversing-> {B[1+1]}",
                "{A[0+3]} <-> {B[0+1]}",
                "{A[0+3]} <-reversing-> {B[0+2]}",
                "{A[0+2]} <-> {B[0+2]}",
                "{A[1+2]} <-reversing-> {B[0+2]}",
                "{A[0+1]} <-reversing-> {B[0+2]}");
    }

    @Test
    void sameEntityWithSubListSizeBounds() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        // The value selector is longer than the number of expected codes because it is expected
        // to be never ending, so it must not be exhausted after the last asserted code.
        int valueSelectorSize = 10;
        int minimumSubListSize = 2;
        int maximumSubListSize = 3;

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a),
                mockEntityIndependentValueSelector(Stream.generate(() -> v1).limit(valueSelectorSize).toArray()),
                mockEntityIndependentValueSelector(Stream.generate(() -> v1).limit(valueSelectorSize).toArray()),
                minimumSubListSize,
                maximumSubListSize,
                false);

        // Alternating left and right subList indexes.
        //      L, R
        TestRandom random = new TestRandom(
                0, 0,
                0, 1,
                0, 2,
                0, 3,
                0, 4,
                1, 3,
                2, 2,
                3, 1,
                4, 0,
                99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "{A[0+3]} <-> {A[0+3]}",
                "{A[0+3]} <-> {A[1+3]}",
                "{A[0+3]} <-> {A[0+2]}",
                "{A[0+3]} <-> {A[1+2]}",
                "{A[0+3]} <-> {A[2+2]}",
                "{A[1+3]} <-> {A[1+2]}",
                "{A[0+2]} <-> {A[0+2]}",
                "{A[1+2]} <-> {A[1+3]}",
                "{A[0+3]} <-> {A[2+2]}"); // equivalent to {A[2+2]} <-> {A[0+3]}

        random.assertIntBoundJustRequested(5);
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

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a),
                // The value selector is longer than the number of expected codes because it is expected
                // to be never ending, so it must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v1, v1, v1),
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

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b, c),
                // The value selector is longer than the number of expected codes because it is expected
                // to be never ending, so it must not be exhausted after the last asserted code.
                mockEntityIndependentValueSelector(v1, v4, v1, v1, v1, v1, v1),
                mockEntityIndependentValueSelector(v4, v1, v1, v1, v1, v1, v1),
                minimumSubListSize,
                maximumSubListSize,
                false);

        // Alternating left and right subList indexes.
        //      L, R
        TestRandom random = new TestRandom(
                0, 0,
                0, 0,
                0, 1,
                1, 0,
                1, 1,
                99, 99);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector,
                "{A[0+2]} <-> {A[0+2]}",
                "{A[0+2]} <-> {A[0+2]}",
                "{A[0+2]} <-> {A[1+2]}",
                "{A[0+2]} <-> {A[1+2]}", // equivalent to {A[1+2]} <-> {A[0+2]}
                "{A[1+2]} <-> {A[1+2]}");

        random.assertIntBoundJustRequested(2);
    }

    @Test
    void sizeUnrestricted() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v4);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                // Selectors must be accurate; their sizes affect the moveSelector size.
                mockEntitySelector(a, b, c),
                mockEntityIndependentValueSelector(v1, v2, v3, v4),
                mockEntityIndependentValueSelector(v1, v2, v3, v4),
                minimumSubListSize,
                maximumSubListSize,
                false);

        TestRandom random = new TestRandom(0, 0);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, 7 * 7);
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

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                // Selectors must be accurate; their sizes affect the moveSelector size.
                mockEntitySelector(a, b, c, d),
                mockEntityIndependentValueSelector(v1, v2, v3, v4, v5, v6, v7, v11, v12, v13, v21, v22, v23, v24),
                mockEntityIndependentValueSelector(v1, v2, v3, v4, v5, v6, v7, v11, v12, v13, v21, v22, v23, v24),
                minimumSubListSize,
                maximumSubListSize,
                false);

        TestRandom random = new TestRandom(0, 0);

        solvingStarted(moveSelector, scoreDirector, random);

        assertCodesOfNeverEndingMoveSelector(moveSelector, 16 * 16);
    }

    @Test
    void phaseLifecycle() {
        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector();
        EntityIndependentValueSelector<TestdataListSolution> leftValueSelector = mockEntityIndependentValueSelector();
        EntityIndependentValueSelector<TestdataListSolution> rightValueSelector = mockEntityIndependentValueSelector();
        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListSwapMoveSelector<TestdataListSolution> moveSelector = new RandomSubListSwapMoveSelector<>(
                getListVariableDescriptor(scoreDirector),
                entitySelector,
                leftValueSelector,
                rightValueSelector,
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

        // The invocation counts are multiplied because both the move selector and its nested subList selectors have
        // their own phaseLifecycleSupport and both register the given entitySelector and valueSelectors.
        // TODO is it OK for the move selector and subList selectors to share the same entitySelector and valueSelector instances?
        verifyPhaseLifecycle(entitySelector, 3, 3, 6);
        verifyPhaseLifecycle(leftValueSelector, 2, 2, 4);
        verifyPhaseLifecycle(rightValueSelector, 1, 1, 2);
    }
}
