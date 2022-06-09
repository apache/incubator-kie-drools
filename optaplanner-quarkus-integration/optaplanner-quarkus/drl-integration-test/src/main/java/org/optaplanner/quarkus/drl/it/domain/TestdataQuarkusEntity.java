package org.optaplanner.quarkus.drl.it.domain;

import java.util.Objects;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class TestdataQuarkusEntity {
    @PlanningVariable(valueRangeProviderRefs = "leftValueRange")
    public String leftValue;

    @PlanningVariable(valueRangeProviderRefs = "rightValueRange")
    public String rightValue;

    public String getLeftValue() {
        return leftValue;
    }

    public String getRightValue() {
        return rightValue;
    }

    public void setLeftValue(String leftValue) {
        this.leftValue = leftValue;
    }

    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }

    public String getFullValue() {
        return Objects.requireNonNullElse(leftValue, "") + Objects.requireNonNullElse(rightValue, "");
    }

    @Override
    public String toString() {
        return "TestdataQuarkusEntity(" + "leftValue=" + leftValue + ";rightValue=" + rightValue + ";)";
    }
}
