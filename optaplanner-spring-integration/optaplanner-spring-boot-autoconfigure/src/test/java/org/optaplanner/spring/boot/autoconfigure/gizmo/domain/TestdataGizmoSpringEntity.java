package org.optaplanner.spring.boot.autoconfigure.gizmo.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class TestdataGizmoSpringEntity {

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public String value;

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
