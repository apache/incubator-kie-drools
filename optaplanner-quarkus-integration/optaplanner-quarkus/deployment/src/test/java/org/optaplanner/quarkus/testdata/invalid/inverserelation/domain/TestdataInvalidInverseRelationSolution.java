package org.optaplanner.quarkus.testdata.invalid.inverserelation.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class TestdataInvalidInverseRelationSolution {

    private List<TestdataInvalidInverseRelationValue> valueList;
    private List<TestdataInvalidInverseRelationEntity> entityList;

    private SimpleScore score;

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataInvalidInverseRelationValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataInvalidInverseRelationValue> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataInvalidInverseRelationEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataInvalidInverseRelationEntity> entityList) {
        this.entityList = entityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
