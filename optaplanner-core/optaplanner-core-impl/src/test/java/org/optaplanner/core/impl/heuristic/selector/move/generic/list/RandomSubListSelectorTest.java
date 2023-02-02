package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.heuristic.selector.move.generic.list.TriangularNumbers.nthTriangle;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.listSize;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockNeverEndingEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
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

class RandomSubListSelectorTest {

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
        int subListCount = 10;

        // The number of subLists of [1, 2, 3, 4] is the 4th triangular number (10).
        assertThat(subListCount).isEqualTo(nthTriangle(listSize(a)) + nthTriangle(listSize(b)));

        RandomSubListSelector<TestdataListSolution> selector = new RandomSubListSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b),
                mockNeverEndingEntityIndependentValueSelector(v1),
                minimumSubListSize,
                maximumSubListSize);

        TestRandom random = new TestRandom(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 99);

        solvingStarted(selector, scoreDirector, random);

        // Every possible subList is selected.
        assertCodesOfNeverEndingIterableSelector(selector, subListCount,
                "A[0+4]",
                "A[0+3]", "A[1+3]",
                "A[0+2]", "A[1+2]", "A[2+2]",
                "A[0+1]", "A[1+1]", "A[2+1]", "A[3+1]");
        random.assertIntBoundJustRequested(subListCount);
    }

    @Test
    void randomWithSubListSizeBounds() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3, v4, v5);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;
        int maximumSubListSize = 3;
        int subListCount = 15 - 5 - 3;

        RandomSubListSelector<TestdataListSolution> selector = new RandomSubListSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a, b),
                mockNeverEndingEntityIndependentValueSelector(v1),
                minimumSubListSize,
                maximumSubListSize);

        TestRandom random = new TestRandom(0, 1, 2, 3, 4, 5, 6, 99);

        solvingStarted(selector, scoreDirector, random);

        // Every possible subList is selected.
        assertCodesOfNeverEndingIterableSelector(selector, subListCount,
                "A[0+3]", "A[1+3]", "A[2+3]",
                "A[0+2]", "A[1+2]", "A[2+2]", "A[3+2]");
        random.assertIntBoundJustRequested(subListCount);
    }

    @Test
    void emptyWhenMinimumSubListSizeGreaterThanListSize() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2, v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 4;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListSelector<TestdataListSolution> selector = new RandomSubListSelector<>(
                getListVariableDescriptor(scoreDirector),
                mockEntitySelector(a),
                mockNeverEndingEntityIndependentValueSelector(),
                minimumSubListSize,
                maximumSubListSize);

        solvingStarted(selector, scoreDirector);

        assertEmptyNeverEndingIterableSelector(selector, 0);
    }

    @Test
    void phaseLifecycle() {
        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                PlannerTestUtils.mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector();
        EntityIndependentValueSelector<TestdataListSolution> valueSelector = mockNeverEndingEntityIndependentValueSelector();

        int minimumSubListSize = 1;
        int maximumSubListSize = Integer.MAX_VALUE;

        RandomSubListSelector<TestdataListSolution> selector = new RandomSubListSelector<>(
                getListVariableDescriptor(scoreDirector),
                entitySelector,
                valueSelector,
                minimumSubListSize,
                maximumSubListSize);

        SolverScope<TestdataListSolution> solverScope = solvingStarted(selector, scoreDirector);
        AbstractPhaseScope<TestdataListSolution> phaseScope = phaseStarted(selector, solverScope);

        AbstractStepScope<TestdataListSolution> stepScope1 = stepStarted(selector, phaseScope);
        selector.stepEnded(stepScope1);

        AbstractStepScope<TestdataListSolution> stepScope2 = stepStarted(selector, phaseScope);
        selector.stepEnded(stepScope2);

        selector.phaseEnded(phaseScope);
        selector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 2);
        verifyPhaseLifecycle(valueSelector, 1, 1, 2);
    }

    @Test
    void validateConstructorArguments() {
        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();
        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector();
        EntityIndependentValueSelector<TestdataListSolution> valueSelector = mockNeverEndingEntityIndependentValueSelector();

        assertThatIllegalArgumentException().isThrownBy(() -> new RandomSubListSelector<>(
                listVariableDescriptor, entitySelector, valueSelector, 0, 5))
                .withMessageContaining("greater than 0");
        assertThatIllegalArgumentException().isThrownBy(() -> new RandomSubListSelector<>(
                listVariableDescriptor, entitySelector, valueSelector, 2, 1))
                .withMessageContaining("less than or equal to the maximum");
        assertThatNoException().isThrownBy(() -> new RandomSubListSelector<>(
                listVariableDescriptor, entitySelector, valueSelector, 1, 1));
    }
}
