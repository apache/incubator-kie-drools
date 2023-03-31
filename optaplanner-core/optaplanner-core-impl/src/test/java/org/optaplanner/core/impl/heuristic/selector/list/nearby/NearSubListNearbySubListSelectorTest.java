package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockReplayingSubListSelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.list.RandomSubListSelector;
import org.optaplanner.core.impl.heuristic.selector.list.SubList;
import org.optaplanner.core.impl.heuristic.selector.list.mimic.MimicReplayingSubListSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.list.TestDistanceMeter;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testutil.TestNearbyRandom;
import org.optaplanner.core.impl.testutil.TestRandom;

class NearSubListNearbySubListSelectorTest {

    @Test
    void randomSelectionUnrestricted() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListValue v4 = new TestdataListValue("60");
        TestdataListValue v5 = new TestdataListValue("75");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("B", v5);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        // Used to populate the distance matrix with destinations.
        RandomSubListSelector<TestdataListSolution> childSubListSelector = new Builder(scoreDirector)
                .withEntities(e1, e2)
                .withValues(v1, v2, v3, v4, v5)
                .build();

        // The replaying selector determines the destination matrix origin.
        // In this case, the origin is v5 (because B[0]=v5) in each iteration.
        MimicReplayingSubListSelector<TestdataListSolution> mockReplayingSubListSelector =
                mockReplayingSubListSelector(childSubListSelector.getVariableDescriptor(),
                        subList(e2, 0), // => v5
                        subList(e2, 0),
                        subList(e2, 0),
                        subList(e2, 0));

        NearSubListNearbySubListSelector<TestdataListSolution> nearbySubListSelector =
                new NearSubListNearbySubListSelector<>(childSubListSelector, mockReplayingSubListSelector,
                        new TestDistanceMeter(), new TestNearbyRandom());

        // Each row is consumed by one next() call of the RandomSubListNearbySubListIterator.
        // The first number in each row becomes the index of a destination in the nearby matrix.
        // So, in this case, we always select the given origin's (v5) 3rd nearest destination (v2).
        // The second number determines the "right" subList size.
        TestRandom testRandom = new TestRandom(
                3, 0,
                3, 1,
                3, 2);

        // A[0]=v1(10)
        // A[1]=v2(45) <= destination
        // A[2]=v3(50)
        // A[3]=v4(60)
        // B[0]=v5(75) <= origin

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbySubListSelector, scoreDirector, testRandom);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbySubListSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbySubListSelector, phaseScopeA);
        assertCodesOfNeverEndingIterableSelector(nearbySubListSelector, childSubListSelector.getSize(),
                // The SubList's assertable code means Entity[fromIndex+subListLength].
                "A[1+1]", "A[1+2]", "A[1+3]");
        nearbySubListSelector.stepEnded(stepScopeA1);
        nearbySubListSelector.phaseEnded(phaseScopeA);
        nearbySubListSelector.solvingEnded(solverScope);
    }

    @Test
    void randomSelectionWithMinMaxSubListSize() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListValue v4 = new TestdataListValue("60");
        TestdataListValue v5 = new TestdataListValue("75");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("B", v5);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;
        int maximumSubListSize = 3;

        // Used to populate the distance matrix with destinations.
        RandomSubListSelector<TestdataListSolution> childSubListSelector = new Builder(scoreDirector)
                .withMinimumSubListSize(minimumSubListSize)
                .withMaximumSubListSize(maximumSubListSize)
                .withEntities(e1, e2)
                .withValues(v1, v2, v3, v4, v5)
                .build();

        // The origin selector determines the destination matrix origin.
        // In this case, the origin is v5 (because B[0]=v5) in each iteration.
        MimicReplayingSubListSelector<TestdataListSolution> mockReplayingSubListSelector =
                mockReplayingSubListSelector(childSubListSelector.getVariableDescriptor(),
                        subList(e2, 0), // => v5
                        subList(e2, 0),
                        subList(e2, 0),
                        subList(e2, 0),
                        subList(e2, 0));

        NearSubListNearbySubListSelector<TestdataListSolution> nearbySubListSelector =
                new NearSubListNearbySubListSelector<>(childSubListSelector, mockReplayingSubListSelector,
                        new TestDistanceMeter(), new TestNearbyRandom());

        // Each row is consumed by one next() call of the RandomSubListNearbySubListIterator.
        // The first number in each row becomes the index of a destination in the nearby matrix.
        // So, in this case, we always select the given origin's (v5) 4th nearest destination (v1).
        // The second number determines the "right" subList size.
        TestRandom testRandom = new TestRandom(
                4, 0,
                4, 1,
                4, 1,
                4, 0);

        // A[0]=v1(10) <= destination
        // A[1]=v2(45)
        // A[2]=v3(50)
        // A[3]=v4(60)
        // B[0]=v5(75) <= origin

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbySubListSelector, scoreDirector, testRandom);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbySubListSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbySubListSelector, phaseScopeA);
        assertCodesOfNeverEndingIterableSelector(nearbySubListSelector, childSubListSelector.getSize(),
                // The SubList's assertable code means Entity[fromIndex+subListLength].
                "A[0+2]", "A[0+3]", "A[0+3]", "A[0+2]");
        nearbySubListSelector.stepEnded(stepScopeA1);
        nearbySubListSelector.phaseEnded(phaseScopeA);
        nearbySubListSelector.solvingEnded(solverScope);

        testRandom.assertIntBoundJustRequested(maximumSubListSize - minimumSubListSize);
    }

    @Test
    void avoidUsingRandomWhenOnlySingleSubListIsPossible() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("A", v1, v2);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("B", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        int minimumSubListSize = 2;

        // Used to populate the distance matrix with destinations.
        RandomSubListSelector<TestdataListSolution> childSubListSelector = new Builder(scoreDirector)
                .withMinimumSubListSize(minimumSubListSize)
                .withValues(v1, v2, v3)
                .withEntities(e1, e2)
                .build();

        // The origin selector determines the destination matrix origin.
        // In this case, the origin is v3 (because B[0]=v3).
        MimicReplayingSubListSelector<TestdataListSolution> mockReplayingSubListSelector =
                mockReplayingSubListSelector(childSubListSelector.getVariableDescriptor(), subList(e2, 0), subList(e2, 0));

        NearSubListNearbySubListSelector<TestdataListSolution> nearbySubListSelector =
                new NearSubListNearbySubListSelector<>(childSubListSelector, mockReplayingSubListSelector,
                        new TestDistanceMeter(), new TestNearbyRandom());

        // 2 is the nearbyIndex and selects v1 from the distance matrix. No other random numbers are needed because:
        //   1. minimumSubListSize = 2,
        //   2. there is only one subList of length 2 beginning with v1 in A[v1, v2].
        // Using random to select the only possible subList in this situation is not only redundant; it would fail because
        // the bound in `random.nextInt(bound)` must be greater than zero.
        TestRandom testRandom = new TestRandom(2); // => v1

        // A[0]=v1(10) <= destination
        // A[1]=v2(45)
        // B[0]=v3(50) <= origin

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbySubListSelector, scoreDirector, testRandom);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbySubListSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbySubListSelector, phaseScopeA);
        assertCodesOfNeverEndingIterableSelector(nearbySubListSelector, childSubListSelector.getSize(), "A[0+2]");
        nearbySubListSelector.stepEnded(stepScopeA1);
        nearbySubListSelector.phaseEnded(phaseScopeA);
        nearbySubListSelector.solvingEnded(solverScope);

        testRandom.assertIntBoundJustRequested(3);
    }

    @Test
    void iteratorShouldBeEmptyIfChildSubListSelectorIsEmpty() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("A", v1, v2);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("B", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        // Used to populate the distance matrix with destinations.
        RandomSubListSelector<TestdataListSolution> childSubListSelector = new Builder(scoreDirector)
                .withMinimumSubListSize(3)
                .withValues(v1, v2, v3)
                .withEntities(e1, e2)
                .build();

        // The origin selector determines the destination matrix origin.
        // In this case, the origin is v3 (because B[0]=v3).
        MimicReplayingSubListSelector<TestdataListSolution> mockReplayingSubListSelector =
                mockReplayingSubListSelector(childSubListSelector.getVariableDescriptor(), subList(e2, 0));

        NearSubListNearbySubListSelector<TestdataListSolution> nearbySubListSelector =
                new NearSubListNearbySubListSelector<>(childSubListSelector, mockReplayingSubListSelector,
                        new TestDistanceMeter(), new TestNearbyRandom());

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbySubListSelector, scoreDirector);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbySubListSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbySubListSelector, phaseScopeA);
        assertEmptyNeverEndingIterableSelector(nearbySubListSelector, 0);
        nearbySubListSelector.stepEnded(stepScopeA1);
        nearbySubListSelector.phaseEnded(phaseScopeA);
        nearbySubListSelector.solvingEnded(solverScope);
    }

    static SubList subList(TestdataListEntity entity, int index) {
        return new SubList(entity, index, 1);
    }

    static class Builder {
        private final InnerScoreDirector<TestdataListSolution, ?> scoreDirector;
        private int minimumSubListSize = 1;
        private int maximumSubListSize = Integer.MAX_VALUE;
        private Object[] entities = new Object[] {};
        private Object[] values = new Object[] {};

        Builder(InnerScoreDirector<TestdataListSolution, ?> scoreDirector) {
            this.scoreDirector = scoreDirector;
        }

        Builder withMinimumSubListSize(int minimumSubListSize) {
            this.minimumSubListSize = minimumSubListSize;
            return this;
        }

        Builder withMaximumSubListSize(int maximumSubListSize) {
            this.maximumSubListSize = maximumSubListSize;
            return this;
        }

        Builder withEntities(Object... entities) {
            this.entities = entities;
            return this;
        }

        Builder withValues(Object... values) {
            this.values = values;
            return this;
        }

        RandomSubListSelector<TestdataListSolution> build() {
            // Enumerates all values. Does not affect nearby subList selection.
            EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                    mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), values);

            // Enumerates all entities. Does not affect nearby subList selection.
            EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(entities);
            when(entitySelector.getEntityDescriptor()).thenReturn(TestdataListEntity.buildEntityDescriptor());

            // Used to populate the distance matrix with destinations.
            return new RandomSubListSelector<>(
                    entitySelector,
                    valueSelector,
                    minimumSubListSize,
                    maximumSubListSize);
        }
    }
}
