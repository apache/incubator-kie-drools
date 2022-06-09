package org.optaplanner.core.impl.testdata.domain.list.valuerange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataListEntityWithArrayValueRange extends TestdataObject {

    public static EntityDescriptor<TestdataListSolutionWithArrayValueRange> buildEntityDescriptor() {
        return TestdataListSolutionWithArrayValueRange.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataListEntityWithArrayValueRange.class);
    }

    public static GenuineVariableDescriptor<TestdataListSolutionWithArrayValueRange> buildVariableDescriptorForValueList() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("valueList");
    }

    @PlanningListVariable(valueRangeProviderRefs = "arrayValueRange")
    private final List<TestdataValue> valueList;

    public TestdataListEntityWithArrayValueRange(String code, List<TestdataValue> valueList) {
        super(code);
        this.valueList = valueList;
    }

    public TestdataListEntityWithArrayValueRange(String code, TestdataValue... values) {
        this(code, new ArrayList<>(Arrays.asList(values)));
    }

    public List<TestdataValue> getValueList() {
        return valueList;
    }
}
