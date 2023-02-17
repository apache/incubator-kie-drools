package org.optaplanner.test.api.score.stream.testdata;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public final class TestdataConstraintVerifierSecondEntity {
    @PlanningId
    private String planningId;

    @PlanningVariable(valueRangeProviderRefs = "stringValueRange")
    private String value;

    public TestdataConstraintVerifierSecondEntity(String planningId, String value) {
        this.planningId = planningId;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
