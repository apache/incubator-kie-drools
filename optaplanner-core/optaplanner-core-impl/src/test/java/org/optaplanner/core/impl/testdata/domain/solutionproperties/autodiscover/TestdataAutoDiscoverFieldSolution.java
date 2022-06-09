package org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover;

import java.util.List;

import org.optaplanner.core.api.domain.autodiscover.AutoDiscoverMemberType;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfiguration;

@PlanningSolution(autoDiscoverMemberType = AutoDiscoverMemberType.FIELD)
public class TestdataAutoDiscoverFieldSolution extends TestdataObject {

    public static SolutionDescriptor<TestdataAutoDiscoverFieldSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataAutoDiscoverFieldSolution.class, TestdataEntity.class);
    }

    private TestdataConstraintConfiguration constraintConfiguration;
    private TestdataObject singleProblemFact;
    @ValueRangeProvider(id = "valueRange")
    private List<TestdataValue> problemFactList;

    private List<TestdataEntity> entityList;
    private TestdataEntity otherEntity;

    private SimpleScore score;

    public TestdataAutoDiscoverFieldSolution() {
    }

    public TestdataAutoDiscoverFieldSolution(String code) {
        super(code);
    }

    public TestdataAutoDiscoverFieldSolution(String code, TestdataObject singleProblemFact,
            List<TestdataValue> problemFactList, List<TestdataEntity> entityList,
            TestdataEntity otherEntity) {
        super(code);
        this.singleProblemFact = singleProblemFact;
        this.problemFactList = problemFactList;
        this.entityList = entityList;
        this.otherEntity = otherEntity;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

}
