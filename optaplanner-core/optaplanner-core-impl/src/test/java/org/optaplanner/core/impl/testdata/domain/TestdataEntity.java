package org.optaplanner.core.impl.testdata.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

@PlanningEntity
public class TestdataEntity extends TestdataObject {

    public static final String VALUE_FIELD = "value";

    public static EntityDescriptor<TestdataSolution> buildEntityDescriptor() {
        return TestdataSolution.buildSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;

    public TestdataEntity() {
    }

    public TestdataEntity(String code) {
        super(code);
    }

    public TestdataEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
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
