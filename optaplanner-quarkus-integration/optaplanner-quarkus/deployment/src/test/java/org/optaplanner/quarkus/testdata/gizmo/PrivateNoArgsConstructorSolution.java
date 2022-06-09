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
    public final SimpleScore score;

    private PrivateNoArgsConstructorSolution() {
        score = null;
    }

    public PrivateNoArgsConstructorSolution(List<PrivateNoArgsConstructorEntity> planningEntityList) {
        this.planningEntityList = planningEntityList;
        score = null;
    }

    @ValueRangeProvider(id = "valueRange")
    public List<String> valueRange() {
        return Arrays.asList("1", "2", "3");
    }
}
