package org.optaplanner.core.impl.testdata.domain.collection;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataArrayBasedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataArrayBasedSolution> buildEntityDescriptor() {
        return TestdataArrayBasedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataArrayBasedEntity.class);
    }

    private TestdataArrayBasedEntity[] entities;

    private TestdataValue value;

    public TestdataArrayBasedEntity() {
    }

    public TestdataArrayBasedEntity(String code) {
        super(code);
    }

    public TestdataArrayBasedEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    public TestdataArrayBasedEntity[] getEntities() {
        return entities;
    }

    public void setEntities(TestdataArrayBasedEntity[] entities) {
        this.entities = entities;
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
