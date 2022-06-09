package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
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

class RandomListSwapIteratorTest {

    @Test
    void iterator() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity.createWithValues("A", v1, v2);
        TestdataListEntity.createWithValues("B");
        TestdataListEntity.createWithValues("C", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());
        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor = getListVariableDescriptor(scoreDirector);

        RandomListSwapIterator<TestdataListSolution> randomListSwapIterator = new RandomListSwapIterator<>(
                listVariableDescriptor,
                scoreDirector.getSupplyManager().demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor)),
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(listVariableDescriptor)),
                mockEntityIndependentValueSelector(v1, v1, v1, v3),
                mockEntityIndependentValueSelector(v1, v2, v3, v1));

        assertCodesOfIterator(randomListSwapIterator,
                "1 {A[0]} <-> 1 {A[0]}",
                "1 {A[0]} <-> 2 {A[1]}",
                "1 {A[0]} <-> 3 {C[0]}",
                "3 {C[0]} <-> 1 {A[0]}");
    }
}
