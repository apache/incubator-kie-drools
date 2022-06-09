package org.optaplanner.core.impl.testdata.domain.clone.customcloner;

import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution(solutionCloner = TestdataScoreNotClonedSolution.class)
public class TestdataScoreNotClonedSolution implements SolutionCloner<TestdataScoreNotClonedSolution> {

    @PlanningScore
    private SimpleScore score;
    @PlanningEntityProperty
    private TestdataEntity entity = new TestdataEntity("A");

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> valueRange() {
        return Collections.singletonList(new TestdataValue("1"));
    }

    @Override
    public TestdataScoreNotClonedSolution cloneSolution(TestdataScoreNotClonedSolution original) {
        TestdataScoreNotClonedSolution clone = new TestdataScoreNotClonedSolution();
        clone.entity.setValue(original.entity.getValue());
        return clone;
    }

}
