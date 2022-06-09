package org.optaplanner.core.impl.testdata.domain.multientity;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataLeadEntity extends TestdataObject {

    public static EntityDescriptor<TestdataMultiEntitySolution> buildEntityDescriptor() {
        return TestdataMultiEntitySolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataLeadEntity.class);
    }

    private TestdataValue value;

    public TestdataLeadEntity() {
    }

    public TestdataLeadEntity(String code) {
        super(code);
    }

    public TestdataLeadEntity(String code, TestdataValue value) {
        super(code);
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
