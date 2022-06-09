package org.optaplanner.core.impl.testdata.domain.collection;

import java.util.Set;

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
public class TestdataSetBasedSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataSetBasedSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataSetBasedSolution.class, TestdataSetBasedEntity.class);
    }

    private Set<TestdataValue> valueSet;
    private Set<TestdataSetBasedEntity> entitySet;

    private SimpleScore score;

    public TestdataSetBasedSolution() {
    }

    public TestdataSetBasedSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public Set<TestdataValue> getValueSet() {
        return valueSet;
    }

    public void setValueSet(Set<TestdataValue> valueSet) {
        this.valueSet = valueSet;
    }

    @PlanningEntityCollectionProperty
    public Set<TestdataSetBasedEntity> getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(Set<TestdataSetBasedEntity> entitySet) {
        this.entitySet = entitySet;
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
