package org.optaplanner.quarkus.it.reflection.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class TestdataReflectionEntity {

    @PlanningVariable(valueRangeProviderRefs = "fieldValueRange")
    public String fieldValue;

    private String methodValueField;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @PlanningVariable(valueRangeProviderRefs = "methodValueRange")
    public String getMethodValue() {
        return methodValueField;
    }

    public void setMethodValue(String methodValueField) {
        this.methodValueField = methodValueField;
    }

}
