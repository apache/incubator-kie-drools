package org.optaplanner.core.impl.testdata.domain.solutionproperties.invalid;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataUnsupportedWildcardSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataUnsupportedWildcardSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataUnsupportedWildcardSolution.class,
                TestdataEntity.class);
    }

    private List<TestdataValue> valueList;
    private List<? super TestdataEntity> supersEntityList;

    private SimpleScore score;

    public TestdataUnsupportedWildcardSolution() {
    }

    public TestdataUnsupportedWildcardSolution(String code) {
        super(code);
    }

    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataValue> valueList) {
        this.valueList = valueList;
    }

    @PlanningEntityCollectionProperty
    public List<? super TestdataEntity> getSupersEntityList() {
        return supersEntityList;
    }

    public void setSupersEntityList(List<? super TestdataEntity> supersEntityList) {
        this.supersEntityList = supersEntityList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
