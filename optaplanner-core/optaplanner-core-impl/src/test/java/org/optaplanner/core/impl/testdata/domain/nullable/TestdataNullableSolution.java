package org.optaplanner.core.impl.testdata.domain.nullable;

import java.util.ArrayList;
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
public class TestdataNullableSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataNullableSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataNullableSolution.class, TestdataNullableEntity.class);
    }

    public static TestdataNullableSolution generateSolution() {
        return generateSolution(2, 2);
    }

    public static TestdataNullableSolution generateSolution(int valueListSize, int entityListSize) {
        TestdataNullableSolution solution = new TestdataNullableSolution("Generated Solution 0");
        List<TestdataValue> valueList = new ArrayList<>(valueListSize);
        for (int i = 0; i < valueListSize; i++) {
            TestdataValue value = new TestdataValue("Generated Value " + i);
            valueList.add(value);
        }
        solution.setValueList(valueList);
        List<TestdataNullableEntity> entityList = new ArrayList<>(entityListSize);
        entityList.add(new TestdataNullableEntity("Generated Entity 0", null));
        for (int i = 1; i < entityListSize; i++) {
            TestdataValue value = valueList.get(i % valueListSize);
            TestdataNullableEntity entity = new TestdataNullableEntity("Generated Entity " + i, value);
            entityList.add(entity);
        }
        solution.setEntityList(entityList);
        return solution;
    }

    private List<TestdataValue> valueList;
    private List<TestdataNullableEntity> entityList;

    private SimpleScore score;

    public TestdataNullableSolution() {
    }

    public TestdataNullableSolution(String code) {
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
    public List<TestdataNullableEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataNullableEntity> entityList) {
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
