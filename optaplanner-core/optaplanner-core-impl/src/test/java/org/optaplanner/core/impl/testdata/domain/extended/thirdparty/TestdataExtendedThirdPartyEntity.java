package org.optaplanner.core.impl.testdata.domain.extended.thirdparty;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataExtendedThirdPartyEntity extends TestdataThirdPartyEntityPojo {

    public static EntityDescriptor<TestdataExtendedThirdPartySolution> buildEntityDescriptor() {
        return TestdataExtendedThirdPartySolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataThirdPartyEntityPojo.class);
    }

    public static GenuineVariableDescriptor<TestdataExtendedThirdPartySolution> buildVariableDescriptorForValue() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("value");
    }

    private Object extraObject;

    public TestdataExtendedThirdPartyEntity() {
    }

    public TestdataExtendedThirdPartyEntity(String code) {
        super(code);
    }

    public TestdataExtendedThirdPartyEntity(String code, TestdataValue value) {
        super(code, value);
    }

    public TestdataExtendedThirdPartyEntity(String code, TestdataValue value, Object extraObject) {
        super(code, value);
        this.extraObject = extraObject;
    }

    public Object getExtraObject() {
        return extraObject;
    }

    public void setExtraObject(Object extraObject) {
        this.extraObject = extraObject;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getValue() {
        return super.getValue();
    }

}
