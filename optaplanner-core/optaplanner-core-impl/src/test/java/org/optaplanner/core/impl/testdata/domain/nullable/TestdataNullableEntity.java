package org.optaplanner.core.impl.testdata.domain.nullable;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataNullableEntity extends TestdataObject {

    public static EntityDescriptor<TestdataNullableSolution> buildEntityDescriptor() {
        return TestdataNullableSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataNullableEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataNullableSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;

    public TestdataNullableEntity() {
    }

    public TestdataNullableEntity(String code) {
        super(code);
    }

    public TestdataNullableEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange", nullable = true)
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
