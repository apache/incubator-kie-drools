package org.optaplanner.quarkus.testdata.gizmo;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@PlanningSolution
public class TestDataKitchenSinkSolution {

    @PlanningEntityProperty
    private TestDataKitchenSinkEntity planningEntityProperty;

    @PlanningEntityCollectionProperty
    private List<TestDataKitchenSinkEntity> planningEntityListProperty;

    @ProblemFactProperty
    private String problemFactProperty;

    @ProblemFactCollectionProperty
    private List<String> problemFactListProperty;

    @PlanningScore
    private HardSoftLongScore score;

    public TestDataKitchenSinkSolution() {

    }

    public TestDataKitchenSinkSolution(TestDataKitchenSinkEntity planningEntityProperty,
            List<TestDataKitchenSinkEntity> planningEntityListProperty, String problemFactProperty,
            List<String> problemFactListProperty, HardSoftLongScore score) {
        this.planningEntityProperty = planningEntityProperty;
        this.planningEntityListProperty = planningEntityListProperty;
        this.problemFactProperty = problemFactProperty;
        this.problemFactListProperty = problemFactListProperty;
        this.score = score;
    }

    public TestDataKitchenSinkEntity getPlanningEntityProperty() {
        return planningEntityProperty;
    }
}
