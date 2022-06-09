package org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataEntityProvidingSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataEntityProvidingSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataEntityProvidingSolution.class,
                TestdataEntityProvidingEntity.class);
    }

    private List<TestdataEntityProvidingEntity> entityList;

    private SimpleScore score;

    public TestdataEntityProvidingSolution() {
    }

    public TestdataEntityProvidingSolution(String code) {
        super(code);
    }

    @PlanningEntityCollectionProperty
    public List<TestdataEntityProvidingEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataEntityProvidingEntity> entityList) {
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

    @ProblemFactCollectionProperty
    public Collection<TestdataValue> getProblemFacts() {
        Set<TestdataValue> valueSet = new HashSet<>();
        for (TestdataEntityProvidingEntity entity : entityList) {
            valueSet.addAll(entity.getValueRange());
        }
        return valueSet;
    }

}
