package org.optaplanner.quarkus.drl.it.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class TestdataQuarkusSolution {

    private List<String> leftValueList;
    private List<String> rightValueList;
    private List<TestdataQuarkusEntity> entityList;

    private SimpleScore score;

    @ValueRangeProvider(id = "leftValueRange")
    @ProblemFactCollectionProperty
    public List<String> getLeftValueList() {
        return leftValueList;
    }

    public void setLeftValueList(List<String> valueList) {
        this.leftValueList = valueList;
    }

    @ValueRangeProvider(id = "rightValueRange")
    @ProblemFactCollectionProperty
    public List<String> getRightValueList() {
        return rightValueList;
    }

    public void setRightValueList(List<String> valueList) {
        this.rightValueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<TestdataQuarkusEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataQuarkusEntity> entityList) {
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
