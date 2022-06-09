package org.optaplanner.core.impl.testdata.domain.list.mixed;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataMixedVariablesSolution {

    public static SolutionDescriptor<TestdataMixedVariablesSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataMixedVariablesSolution.class,
                TestdataMixedVariablesEntity.class);
    }

    private List<TestdataValue> valueList;
    private TestdataMixedVariablesEntity entity;
    private SimpleScore score;

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataValue> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityProperty
    public TestdataMixedVariablesEntity getEntity() {
        return entity;
    }

    public void setEntity(TestdataMixedVariablesEntity entity) {
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
