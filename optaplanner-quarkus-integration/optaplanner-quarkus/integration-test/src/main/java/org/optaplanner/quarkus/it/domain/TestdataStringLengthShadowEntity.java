package org.optaplanner.quarkus.it.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

@PlanningEntity
public class TestdataStringLengthShadowEntity {

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private String value;

    @CustomShadowVariable(variableListenerClass = StringLengthVariableListener.class,
            sources = {
                    @PlanningVariableReference(entityClass = TestdataStringLengthShadowEntity.class,
                            variableName = "value")
            })
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
