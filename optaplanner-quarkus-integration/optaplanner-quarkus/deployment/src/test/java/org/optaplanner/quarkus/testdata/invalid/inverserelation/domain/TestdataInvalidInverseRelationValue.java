package org.optaplanner.quarkus.testdata.invalid.inverserelation.domain;

import java.util.List;

import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

public class TestdataInvalidInverseRelationValue {
    @InverseRelationShadowVariable(
            sourceVariableName = "value")
    private List<TestdataInvalidInverseRelationEntity> entityList;

    public List<TestdataInvalidInverseRelationEntity> getEntityList() {
        return entityList;
    }
}
