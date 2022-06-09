package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.getListVariableDescriptor;
import static org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class OriginalListSwapIteratorTest {

    private final TestdataListValue v1 = new TestdataListValue("v1");
    private final TestdataListValue v2 = new TestdataListValue("v2");

    @Test
    void emptyLeftValueSelector() {
        assertEmptyIterator(emptyList(), asList(v1, v2));
    }

    @Test
    void emptyRightValueSelector() {
        assertEmptyIterator(asList(v1, v2), emptyList());
    }

    static void assertEmptyIterator(List<Object> leftValues, List<Object> rightValues) {
        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataListSolution.buildSolutionDescriptor());
        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor = getListVariableDescriptor(scoreDirector);
        OriginalListSwapIterator<TestdataListSolution> listSwapIterator = new OriginalListSwapIterator<>(
                listVariableDescriptor,
                scoreDirector.getSupplyManager().demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor)),
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(listVariableDescriptor)),
                mockEntityIndependentValueSelector(leftValues.toArray()),
                mockEntityIndependentValueSelector(rightValues.toArray()));

        assertThat(listSwapIterator).isExhausted();
    }
}
