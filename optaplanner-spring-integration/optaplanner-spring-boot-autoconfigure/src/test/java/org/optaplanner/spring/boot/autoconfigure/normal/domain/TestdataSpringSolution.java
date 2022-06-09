package org.optaplanner.spring.boot.autoconfigure.normal.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class TestdataSpringSolution {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "valueRange")
    private List<String> valueList;
    @PlanningEntityCollectionProperty
    private List<TestdataSpringEntity> entityList;

    @PlanningScore
    private SimpleScore score;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public List<TestdataSpringEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataSpringEntity> entityList) {
        this.entityList = entityList;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }
}
