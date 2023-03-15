package org.optaplanner.quarkus.testdata.gizmo;

import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

@PlanningSolution
public class PrivateNoArgsConstructorSolution {
    @PlanningEntityCollectionProperty
    List<PrivateNoArgsConstructorEntity> planningEntityList;

    @PlanningScore
    public SimpleScore score;

    public final int someField;

    private PrivateNoArgsConstructorSolution() {
        this.someField = 1;
    }

    public PrivateNoArgsConstructorSolution(List<PrivateNoArgsConstructorEntity> planningEntityList) {
        this.planningEntityList = planningEntityList;
        this.someField = 2;
    }

    @ValueRangeProvider(id = "valueRange")
    public List<String> valueRange() {
        return Arrays.asList("1", "2", "3");
    }
}
