package org.optaplanner.core.impl.testdata.domain.pinned.extended;

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
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;

@PlanningSolution
public class TestdataExtendedPinnedSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataExtendedPinnedSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataExtendedPinnedSolution.class,
                TestdataPinnedEntity.class, TestdataExtendedPinnedEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<TestdataValue> subValueList;
    private List<TestdataPinnedEntity> entityList;

    private SimpleScore score;

    public TestdataExtendedPinnedSolution() {
    }

    public TestdataExtendedPinnedSolution(String code) {
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

    @ValueRangeProvider(id = "subValueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> getSubValueList() {
        return subValueList;
    }

    public void setSubValueList(List<TestdataValue> subValueList) {
        this.subValueList = subValueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataPinnedEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataPinnedEntity> entityList) {
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
