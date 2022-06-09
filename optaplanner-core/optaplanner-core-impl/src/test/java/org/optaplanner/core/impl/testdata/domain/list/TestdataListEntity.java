package org.optaplanner.core.impl.testdata.domain.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataListEntity extends TestdataObject {

    public static EntityDescriptor<TestdataListSolution> buildEntityDescriptor() {
        return TestdataListSolution.buildSolutionDescriptor().findEntityDescriptorOrFail(TestdataListEntity.class);
    }

    public static ListVariableDescriptor<TestdataListSolution> buildVariableDescriptorForValueList() {
        return (ListVariableDescriptor<TestdataListSolution>) buildEntityDescriptor().getGenuineVariableDescriptor("valueList");
    }

    public static TestdataListEntity createWithValues(String code, TestdataListValue... values) {
        // Set up shadow variables to preserve consistency.
        return new TestdataListEntity(code, values).setUpShadowVariables();
    }

    TestdataListEntity setUpShadowVariables() {
        valueList.forEach(testdataListValue -> {
            testdataListValue.setEntity(this);
            testdataListValue.setIndex(valueList.indexOf(testdataListValue));
        });
        return this;
    }

    @PlanningListVariable(valueRangeProviderRefs = "valueRange")
    private List<TestdataListValue> valueList;

    public TestdataListEntity() {
    }

    public TestdataListEntity(String code, List<TestdataListValue> valueList) {
        super(code);
        this.valueList = valueList;
    }

    public TestdataListEntity(String code, TestdataListValue... values) {
        this(code, new ArrayList<>(Arrays.asList(values)));
    }

    public List<TestdataListValue> getValueList() {
        return valueList;
    }
}
