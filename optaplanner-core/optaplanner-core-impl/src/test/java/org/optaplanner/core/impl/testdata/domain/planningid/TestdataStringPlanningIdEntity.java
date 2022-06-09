package org.optaplanner.core.impl.testdata.domain.planningid;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class TestdataStringPlanningIdEntity {
    @PlanningId
    private String planningId;

    @PlanningVariable(valueRangeProviderRefs = "stringValueRange")
    private String value;

    public TestdataStringPlanningIdEntity(String planningId) {
        this(planningId, null);
    }

    public TestdataStringPlanningIdEntity(String planningId, String value) {
        this.planningId = planningId;
        this.value = value;
    }

    public String getPlanningId() {
        return planningId;
    }

    public void setPlanningId(String planningId) {
        this.planningId = planningId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
