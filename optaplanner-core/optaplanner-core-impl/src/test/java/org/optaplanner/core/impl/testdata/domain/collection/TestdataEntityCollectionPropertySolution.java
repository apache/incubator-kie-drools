package org.optaplanner.core.impl.testdata.domain.collection;

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
public class TestdataEntityCollectionPropertySolution extends TestdataObject {

    public static SolutionDescriptor<TestdataEntityCollectionPropertySolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataEntityCollectionPropertySolution.class,
                TestdataEntityCollectionPropertyEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<TestdataEntityCollectionPropertyEntity> entityList;

    @PlanningScore
    private SimpleScore score;

    public TestdataEntityCollectionPropertySolution() {
    }

    public TestdataEntityCollectionPropertySolution(String code) {
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
    public List<TestdataEntityCollectionPropertyEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataEntityCollectionPropertyEntity> entityList) {
        this.entityList = entityList;
    }

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
