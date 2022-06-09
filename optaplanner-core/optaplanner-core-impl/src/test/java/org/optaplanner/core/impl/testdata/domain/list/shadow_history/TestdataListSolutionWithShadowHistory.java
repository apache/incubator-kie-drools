package org.optaplanner.core.impl.testdata.domain.list.shadow_history;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

@PlanningSolution
public class TestdataListSolutionWithShadowHistory {

    public static SolutionDescriptor<TestdataListSolutionWithShadowHistory> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(
                TestdataListSolutionWithShadowHistory.class,
                TestdataListEntityWithShadowHistory.class,
                TestdataListValueWithShadowHistory.class);
    }

    private List<TestdataListValueWithShadowHistory> valueList;
    private List<TestdataListEntityWithShadowHistory> entityList;
    private SimpleScore score;

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataListValueWithShadowHistory> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataListValueWithShadowHistory> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataListEntityWithShadowHistory> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataListEntityWithShadowHistory> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }
}
