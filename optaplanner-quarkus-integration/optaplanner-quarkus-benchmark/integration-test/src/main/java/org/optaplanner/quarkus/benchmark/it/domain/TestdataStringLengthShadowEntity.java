package org.optaplanner.quarkus.benchmark.it.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;

@PlanningEntity
public class TestdataStringLengthShadowEntity {

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private String value;

    @ShadowVariable(variableListenerClass = StringLengthVariableListener.class,
            sourceEntityClass = TestdataStringLengthShadowEntity.class, sourceVariableName = "value")
    private Integer length;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

}
