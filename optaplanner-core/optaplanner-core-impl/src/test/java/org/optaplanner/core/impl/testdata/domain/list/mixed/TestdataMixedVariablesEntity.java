package org.optaplanner.core.impl.testdata.domain.list.mixed;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataMixedVariablesEntity extends TestdataObject {

    public static EntityDescriptor<TestdataMixedVariablesSolution> buildEntityDescriptor() {
        return TestdataMixedVariablesSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataMixedVariablesEntity.class);
    }

    public static ListVariableDescriptor<TestdataMixedVariablesSolution> buildVariableDescriptorForValueList() {
        return (ListVariableDescriptor<TestdataMixedVariablesSolution>) buildEntityDescriptor()
                .getGenuineVariableDescriptor("valueList");
    }

    @PlanningListVariable(valueRangeProviderRefs = "valueRange")
    private final List<TestdataValue> valueList;
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataValue value;

    public TestdataMixedVariablesEntity(String code, List<TestdataValue> valueList, TestdataValue value) {
        super(code);
        this.valueList = valueList;
        this.value = value;
    }

    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public TestdataValue getValue() {
        return value;
    }
}
