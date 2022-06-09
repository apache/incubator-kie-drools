package org.optaplanner.core.impl.testdata.domain.clone.customcloner;

import java.util.Arrays;
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

@PlanningSolution(solutionCloner = TestdataCorrectlyClonedSolution.class)
public class TestdataCorrectlyClonedSolution implements SolutionCloner<TestdataCorrectlyClonedSolution> {

    private boolean clonedByCustomCloner = false;
    @PlanningScore
    private SimpleScore score;
    @PlanningEntityProperty
    private TestdataEntity entity = new TestdataEntity("A");

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> valueRange() {
        // two values needed to allow for at least one doable move, otherwise the second step ends in an infinite loop
        return Arrays.asList(new TestdataValue("1"), new TestdataValue("2"));
    }

    @Override
    public TestdataCorrectlyClonedSolution cloneSolution(TestdataCorrectlyClonedSolution original) {
        TestdataCorrectlyClonedSolution clone = new TestdataCorrectlyClonedSolution();
        clone.clonedByCustomCloner = true;
        // score is immutable so no need to create a new instance
        clone.score = original.score;
        clone.entity.setValue(original.entity.getValue());
        return clone;
    }

    public boolean isClonedByCustomCloner() {
        return clonedByCustomCloner;
    }

}
