package org.optaplanner.core.impl.testdata.domain.planningid;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

@PlanningSolution
public class TestdataStringPlanningIdSolution extends TestdataSolution {
    private List<String> stringValueList;
    private List<TestdataStringPlanningIdEntity> stringEntityList;

    @ValueRangeProvider(id = "stringValueRange")
    @ProblemFactCollectionProperty
    public List<String> getStringValueList() {
        return stringValueList;
    }

    public void setStringValueList(List<String> stringValueList) {
        this.stringValueList = stringValueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataStringPlanningIdEntity> getStringEntityList() {
        return stringEntityList;
    }

    public void setStringEntityList(List<TestdataStringPlanningIdEntity> stringEntityList) {
        this.stringEntityList = stringEntityList;
    }
}
