package org.optaplanner.quarkus.testdata.interfaceentity.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class TestdataInterfaceEntitySolution {

    @PlanningEntityCollectionProperty
    List<TestdataInterfaceEntity> entityList;

    @ValueRangeProvider(id = "valueRange")
    List<Integer> valueList;

    @PlanningScore
    SimpleScore score;

    public TestdataInterfaceEntitySolution() {
    }

    public TestdataInterfaceEntitySolution(List<TestdataInterfaceEntity> entityList, List<Integer> valueList) {
        this.entityList = entityList;
        this.valueList = valueList;
    }

    public List<TestdataInterfaceEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataInterfaceEntity> entityList) {
        this.entityList = entityList;
    }

    public List<Integer> getValueList() {
        return valueList;
    }

    public void setValueList(List<Integer> valueList) {
        this.valueList = valueList;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }
}
