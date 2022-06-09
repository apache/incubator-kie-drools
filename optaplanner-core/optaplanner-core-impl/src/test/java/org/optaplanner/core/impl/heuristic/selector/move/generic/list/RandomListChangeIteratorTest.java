package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testutil.TestRandom;
import org.optaplanner.core.impl.util.Pair;

class RandomListChangeIteratorTest {

    @Test
    void iterator() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity a = TestdataListEntity.createWithValues("A", v1, v2);
        TestdataListEntity b = TestdataListEntity.createWithValues("B");
        TestdataListEntity c = TestdataListEntity.createWithValues("C", v3);

        TestRandom random = new TestRandom(2, 3, 0); // global destination indexes
        final int destinationIndexRange = 6; // value count + entity count

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());
        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor = getListVariableDescriptor(scoreDirector);

        RandomListChangeIterator<TestdataListSolution> randomListChangeIterator = new RandomListChangeIterator<>(
                listVariableDescriptor,
                scoreDirector.getSupplyManager().demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor)),
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(listVariableDescriptor)),
                mockEntityIndependentValueSelector(v1, v2, v3), // Iterates over values in this given order.
                mockEntitySelector(a, b, c), // Entity selector is only used to discover the destination index range.
                random);

        // 0 points at A[0]
        assertEntityAndIndex(randomListChangeIterator, 0, a, 0);
        // 1 points at A[1]
        assertEntityAndIndex(randomListChangeIterator, 1, a, 1);
        // 2 points at A[2]
        assertEntityAndIndex(randomListChangeIterator, 2, a, 2);
        // 3 points at B[0]
        assertEntityAndIndex(randomListChangeIterator, 3, b, 0);

        // The moved values (1, 2, 3) and their source positions are supplied by the mocked value selector.
        // The test is focused on the destinations (A[2], B[0], A[0]), which reflect the numbers supplied by the test random.
        assertCodesOfIterator(randomListChangeIterator,
                "1 {A[0]->A[2]}",
                "2 {A[1]->B[0]}",
                "3 {C[0]->A[0]}");

        random.assertIntBoundJustRequested(destinationIndexRange);
    }

    static void assertEntityAndIndex(
            RandomListChangeIterator<TestdataListSolution> randomListChangeIterator,
            int globalIndex,
            Object expectedEntity,
            int expectedListIndex) {
        Pair<Object, Integer> pair = randomListChangeIterator.entityAndIndexFromGlobalIndex(globalIndex);
        assertThat(pair.getKey()).isEqualTo(expectedEntity);
        assertThat(pair.getValue()).isEqualTo(expectedListIndex);
    }
}
