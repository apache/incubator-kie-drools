package org.optaplanner.core.impl.testdata.domain.shadow.manytomany;

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
public class TestdataManyToManyShadowedSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataManyToManyShadowedSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataManyToManyShadowedSolution.class,
                TestdataManyToManyShadowedEntity.class);
    }

    static SolutionDescriptor<TestdataManyToManyShadowedSolution> buildSolutionDescriptorRequiresUniqueEvents() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataManyToManyShadowedSolution.class,
                TestdataManyToManyShadowedEntityUniqueEvents.class);
    }

    private List<TestdataValue> valueList;
    private List<TestdataManyToManyShadowedEntity> entityList;

    private SimpleScore score;

    public TestdataManyToManyShadowedSolution() {
    }

    public TestdataManyToManyShadowedSolution(String code) {
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
    public List<TestdataManyToManyShadowedEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataManyToManyShadowedEntity> entityList) {
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
