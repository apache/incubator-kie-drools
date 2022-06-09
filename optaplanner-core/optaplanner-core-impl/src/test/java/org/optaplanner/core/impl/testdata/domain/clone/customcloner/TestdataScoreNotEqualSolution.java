package org.optaplanner.core.impl.testdata.domain.clone.customcloner;

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

@PlanningSolution(solutionCloner = TestdataScoreNotEqualSolution.class)
public class TestdataScoreNotEqualSolution implements SolutionCloner<TestdataScoreNotEqualSolution> {

    @PlanningScore
    private SimpleScore score;
    @PlanningEntityProperty
    private TestdataEntity entity = new TestdataEntity();

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> valueRange() {
        // solver will never get to this point due to cloning corruption
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TestdataScoreNotEqualSolution cloneSolution(TestdataScoreNotEqualSolution original) {
        TestdataScoreNotEqualSolution clone = new TestdataScoreNotEqualSolution();
        clone.entity.setValue(original.entity.getValue());
        if (original.score != null) {
            clone.score = SimpleScore.ofUninitialized(original.score.getInitScore() - 1, original.score.getScore() - 1);
        } else {
            clone.score = SimpleScore.of(0);
        }
        if (clone.score.equals(original.score)) {
            throw new IllegalStateException("The cloned score should be intentionally unequal to the original score");
        }
        return clone;
    }

}
