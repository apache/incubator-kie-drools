package org.optaplanner.core.impl.testdata.domain.reflect.field;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataFieldAnnotatedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataFieldAnnotatedSolution> buildEntityDescriptor() {
        return TestdataFieldAnnotatedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataFieldAnnotatedEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataFieldAnnotatedSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataValue value;

    public TestdataFieldAnnotatedEntity() {
    }

    public TestdataFieldAnnotatedEntity(String code) {
        super(code);
    }

    public TestdataFieldAnnotatedEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    public TestdataValue getValue() {
        return value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
