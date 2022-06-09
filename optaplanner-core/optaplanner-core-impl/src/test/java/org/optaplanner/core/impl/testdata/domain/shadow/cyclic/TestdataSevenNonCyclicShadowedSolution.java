package org.optaplanner.core.impl.testdata.domain.shadow.cyclic;

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
public class TestdataSevenNonCyclicShadowedSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataSevenNonCyclicShadowedSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataSevenNonCyclicShadowedSolution.class,
                TestdataSevenNonCyclicShadowedEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<TestdataSevenNonCyclicShadowedEntity> entityList;

    private SimpleScore score;

    public TestdataSevenNonCyclicShadowedSolution() {
    }

    public TestdataSevenNonCyclicShadowedSolution(String code) {
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
    public List<TestdataSevenNonCyclicShadowedEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataSevenNonCyclicShadowedEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
