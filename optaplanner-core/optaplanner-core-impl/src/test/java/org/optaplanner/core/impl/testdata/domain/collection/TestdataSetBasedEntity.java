package org.optaplanner.core.impl.testdata.domain.collection;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataSetBasedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataSetBasedSolution> buildEntityDescriptor() {
        return TestdataSetBasedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataSetBasedEntity.class);
    }

    private TestdataValue value;

    public TestdataSetBasedEntity() {
    }

    public TestdataSetBasedEntity(String code) {
        super(code);
    }

    public TestdataSetBasedEntity(String code, TestdataValue value) {
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
