package org.optaplanner.core.impl.testdata.domain.shadow.extended;

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
public class TestdataExtendedShadowedSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataExtendedShadowedSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataExtendedShadowedSolution.class,
                TestdataExtendedShadowedParentEntity.class, TestdataExtendedShadowedChildEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<TestdataExtendedShadowedParentEntity> entityList;

    private SimpleScore score;

    public TestdataExtendedShadowedSolution() {
    }

    public TestdataExtendedShadowedSolution(String code) {
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
    public List<TestdataExtendedShadowedParentEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataExtendedShadowedParentEntity> entityList) {
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
