package org.optaplanner.core.impl.testdata.domain.reflect.generic;

import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataGenericEntity<T> extends TestdataObject {

    public static EntityDescriptor<TestdataGenericSolution> buildEntityDescriptor() {
        return TestdataGenericSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataGenericEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataGenericSolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private TestdataGenericValue<T> value;
    private TestdataGenericValue<T> subTypeValue;
    private TestdataGenericValue<Map<T, TestdataGenericValue<T>>> complexGenericValue;

    public TestdataGenericEntity() {
    }

    public TestdataGenericEntity(String code) {
        super(code);
    }

    public TestdataGenericEntity(String code, TestdataGenericValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataGenericValue<T> getValue() {
        return value;
    }

    @PlanningVariable(valueRangeProviderRefs = "subTypeValueRange")
    public TestdataGenericValue<T> getSubTypeValue() {
        return subTypeValue;
    }

    public void setValue(TestdataGenericValue<T> value) {
        this.value = value;
    }

    public void setSubTypeValue(TestdataGenericValue<T> subTypeValue) {
        this.subTypeValue = subTypeValue;
    }

    @PlanningVariable(valueRangeProviderRefs = "complexGenericValueRange")
    public TestdataGenericValue<Map<T, TestdataGenericValue<T>>> getComplexGenericValue() {
        return complexGenericValue;
    }

    public void setComplexGenericValue(TestdataGenericValue<Map<T, TestdataGenericValue<T>>> complexGenericValue) {
        this.complexGenericValue = complexGenericValue;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
