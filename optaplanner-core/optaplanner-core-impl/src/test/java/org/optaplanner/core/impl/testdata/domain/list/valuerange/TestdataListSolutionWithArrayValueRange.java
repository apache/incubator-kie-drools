package org.optaplanner.core.impl.testdata.domain.list.valuerange;

import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataListSolutionWithArrayValueRange {

    public static SolutionDescriptor<TestdataListSolutionWithArrayValueRange> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(
                TestdataListSolutionWithArrayValueRange.class,
                TestdataListEntityWithArrayValueRange.class);
    }

    private TestdataValue[] valueArray;
    private TestdataListEntityWithArrayValueRange entity;
    private SimpleScore score;

    @ValueRangeProvider(id = "arrayValueRange")
    @ProblemFactCollectionProperty
    public TestdataValue[] getValueArray() {
        return valueArray;
    }

    public void setValueArray(TestdataValue[] valueArray) {
        this.valueArray = valueArray;
    }

    @PlanningEntityProperty
    public TestdataListEntityWithArrayValueRange getEntity() {
        return entity;
    }

    public void setEntity(TestdataListEntityWithArrayValueRange entity) {
        this.entity = entity;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }
}
