package org.optaplanner.quarkus.testdata.invalid.inverserelation.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class TestdataInvalidInverseRelationEntity {

    private TestdataInvalidInverseRelationValue value;

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataInvalidInverseRelationValue getValue() {
        return value;
    }

    public void setValue(TestdataInvalidInverseRelationValue value) {
        this.value = value;
    }

}
