package org.optaplanner.core.impl.testdata.domain.shadow.order;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataShadowVariableOrderSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataShadowVariableOrderSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataShadowVariableOrderSolution.class,
                TestdataShadowVariableOrderEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<TestdataShadowVariableOrderEntity> entityList;

    private SimpleScore score;

    public TestdataShadowVariableOrderSolution() {
    }

    public TestdataShadowVariableOrderSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataValue> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataShadowVariableOrderEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataShadowVariableOrderEntity> entityList) {
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
