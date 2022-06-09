package org.optaplanner.spring.boot.autoconfigure.normal.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class TestdataSpringEntity {

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private String value;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
