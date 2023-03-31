package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockReplayingSubListSelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingIterableSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementDestinationSelector;
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

class NearSubListNearbyDestinationSelectorTest {

    @Test
    void originalSelection() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListValue v4 = new TestdataListValue("60");
        TestdataListValue v5 = new TestdataListValue("75");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("B", v5);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1, v2, v3, v4, v5);

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(e1, e2);
        when(entitySelector.getEntityDescriptor()).thenReturn(TestdataListEntity.buildEntityDescriptor());

        // Used to populate the distance matrix with destinations.
        ElementDestinationSelector<TestdataListSolution> childDestinationSelector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, false);

        // The replaying selector determines the destination matrix origin.
        // In this case, the origin is v2 (because A[1]=v2) in each iteration.
        MimicReplayingSubListSelector<TestdataListSolution> mockReplayingSubListSelector =
                mockReplayingSubListSelector(childDestinationSelector.getVariableDescriptor(),
                        subList(e1, 1), // => v2
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1));

        NearSubListNearbyDestinationSelector<TestdataListSolution> nearbyDestinationSelector =
                new NearSubListNearbyDestinationSelector<>(childDestinationSelector, mockReplayingSubListSelector,
                        new TestDistanceMeter(), null, false);

        // A[0]=v1(10)
        // A[1]=v2(45) <= origin
        // A[2]=v3(50)
        // A[3]=v4(60)
        // B[0]=v5(75)

        // IMPORTANT: For example, when v4(60) is returned from the distance matrix, the ElementRef is A[4]
        // although v4 is at A[3]. It's because the destination is "after" the nearby value (so its index + 1).

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbyDestinationSelector, scoreDirector);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbyDestinationSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbyDestinationSelector, phaseScopeA);
        assertAllCodesOfIterableSelector(nearbyDestinationSelector, entitySelector.getSize() + valueSelector.getSize(),
                // 45      50      60      75      10      0       0
                "A[2]", "A[3]", "A[4]", "B[1]", "A[1]", "A[0]", "B[0]");
        nearbyDestinationSelector.stepEnded(stepScopeA1);
        nearbyDestinationSelector.phaseEnded(phaseScopeA);
        nearbyDestinationSelector.solvingEnded(solverScope);
    }

    @Test
    void randomSelection() {
        TestdataListValue v1 = new TestdataListValue("10");
        TestdataListValue v2 = new TestdataListValue("45");
        TestdataListValue v3 = new TestdataListValue("50");
        TestdataListValue v4 = new TestdataListValue("60");
        TestdataListValue v5 = new TestdataListValue("75");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("A", v1, v2, v3, v4);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("B", v5);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());

        EntityIndependentValueSelector<TestdataListSolution> valueSelector =
                mockEntityIndependentValueSelector(getListVariableDescriptor(scoreDirector), v1, v2, v3, v4, v5);

        EntitySelector<TestdataListSolution> entitySelector = mockEntitySelector(e1, e2);
        when(entitySelector.getEntityDescriptor()).thenReturn(TestdataListEntity.buildEntityDescriptor());

        // Used to populate the distance matrix with destinations.
        ElementDestinationSelector<TestdataListSolution> childDestinationSelector = new ElementDestinationSelector<>(
                entitySelector, valueSelector, true);

        // The replaying selector determines the destination matrix origin.
        // In this case, the origin is v2 (because A[1]=v2) in each iteration.
        MimicReplayingSubListSelector<TestdataListSolution> mockReplayingSubListSelector =
                mockReplayingSubListSelector(childDestinationSelector.getVariableDescriptor(),
                        subList(e1, 1), // => v2
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1),
                        subList(e1, 1));

        NearSubListNearbyDestinationSelector<TestdataListSolution> nearbyDestinationSelector =
                new NearSubListNearbyDestinationSelector<>(childDestinationSelector, mockReplayingSubListSelector,
                        new TestDistanceMeter(), new TestNearbyRandom(), true);

        TestRandom testRandom = new TestRandom(0, 1, 2, 3, 4, 5, 6); // nearbyIndices (=> destinations)

        // A[0]=v1(10)
        // A[1]=v2(45) <= origin
        // A[2]=v3(50)
        // A[3]=v4(60)
        // B[0]=v5(75)

        // IMPORTANT: For example, when v4(60) is returned from the distance matrix, the ElementRef is A[4]
        // although v4 is at A[3]. It's because the destination is "after" the nearby value (so its index + 1).

        SolverScope<TestdataListSolution> solverScope = solvingStarted(nearbyDestinationSelector, scoreDirector, testRandom);
        AbstractPhaseScope<TestdataListSolution> phaseScopeA = phaseStarted(nearbyDestinationSelector, solverScope);
        AbstractStepScope<TestdataListSolution> stepScopeA1 = stepStarted(nearbyDestinationSelector, phaseScopeA);
        assertCodesOfNeverEndingIterableSelector(nearbyDestinationSelector, entitySelector.getSize() + valueSelector.getSize(),
                // 45      50      60      75      10      0       0
                "A[2]", "A[3]", "A[4]", "B[1]", "A[1]", "A[0]", "B[0]");
        nearbyDestinationSelector.stepEnded(stepScopeA1);
        nearbyDestinationSelector.phaseEnded(phaseScopeA);
        nearbyDestinationSelector.solvingEnded(solverScope);
    }

    static SubList subList(TestdataListEntity entity, int index) {
        return new SubList(entity, index, 1);
    }
}
