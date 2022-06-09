package org.optaplanner.core.impl.testdata.domain.collection;

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
public class TestdataArrayBasedSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataArrayBasedSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataArrayBasedSolution.class, TestdataArrayBasedEntity.class);
    }

    private TestdataValue[] values;
    private TestdataArrayBasedEntity[] entities;

    private SimpleScore score;

    public TestdataArrayBasedSolution() {
    }

    public TestdataArrayBasedSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public TestdataValue[] getValues() {
        return values;
    }

    public void setValues(TestdataValue[] values) {
        this.values = values;
    }

    @PlanningEntityCollectionProperty
    public TestdataArrayBasedEntity[] getEntities() {
        return entities;
    }

    public void setEntities(TestdataArrayBasedEntity[] entities) {
        this.entities = entities;
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
