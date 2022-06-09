package org.optaplanner.core.impl.testdata.domain.list;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class TestdataListUtils {

    private TestdataListUtils() {
    }

    public static EntitySelector<TestdataListSolution> mockEntitySelector(Object... entities) {
        return SelectorTestUtils.mockEntitySelector(TestdataListEntity.class, entities);
    }

    public static EntityIndependentValueSelector<TestdataListSolution> mockEntityIndependentValueSelector(Object... values) {
        return SelectorTestUtils.mockEntityIndependentValueSelector(TestdataListEntity.class, "valueList", values);
    }

    public static ListVariableDescriptor<TestdataListSolution> getListVariableDescriptor(
            InnerScoreDirector<TestdataListSolution, ?> scoreDirector) {
        return (ListVariableDescriptor<TestdataListSolution>) scoreDirector
                .getSolutionDescriptor()
                .getEntityDescriptorStrict(TestdataListEntity.class)
                .getGenuineVariableDescriptor("valueList");
    }
}
