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

@PlanningSolution(solutionCloner = TestdataEntitiesNotClonedSolution.class)
public class TestdataEntitiesNotClonedSolution implements SolutionCloner<TestdataEntitiesNotClonedSolution> {

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
    public TestdataEntitiesNotClonedSolution cloneSolution(TestdataEntitiesNotClonedSolution original) {
        TestdataEntitiesNotClonedSolution clone = new TestdataEntitiesNotClonedSolution();
        clone.entity = original.entity;
        clone.score = original.score;
        return clone;
    }

}
