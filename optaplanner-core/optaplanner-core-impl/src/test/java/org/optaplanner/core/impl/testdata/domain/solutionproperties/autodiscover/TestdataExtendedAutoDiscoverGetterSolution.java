package org.optaplanner.core.impl.testdata.domain.solutionproperties.autodiscover;

import java.util.List;

import org.optaplanner.core.api.domain.autodiscover.AutoDiscoverMemberType;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution(autoDiscoverMemberType = AutoDiscoverMemberType.GETTER)
public class TestdataExtendedAutoDiscoverGetterSolution extends TestdataAutoDiscoverGetterSolution {

    public static SolutionDescriptor<TestdataExtendedAutoDiscoverGetterSolution> buildSubclassSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataExtendedAutoDiscoverGetterSolution.class,
                TestdataEntity.class);
    }

    private TestdataObject singleProblemFactFieldOverride;
    private List<TestdataValue> problemFactListFieldOverride;

    private List<TestdataEntity> entityListFieldOverride;
    private TestdataEntity otherEntityFieldOverride;

    public TestdataExtendedAutoDiscoverGetterSolution() {
    }

    public TestdataExtendedAutoDiscoverGetterSolution(String code) {
        super(code);
    }

    public TestdataExtendedAutoDiscoverGetterSolution(String code, TestdataObject singleProblemFact,
            List<TestdataValue> problemFactList, List<TestdataEntity> entityList,
            TestdataEntity otherEntity) {
        super(code);
        this.singleProblemFactFieldOverride = singleProblemFact;
        this.problemFactListFieldOverride = problemFactList;
        this.entityListFieldOverride = entityList;
        this.otherEntityFieldOverride = otherEntity;
    }

    @Override
    public TestdataObject getSingleProblemFact() {
        return singleProblemFactFieldOverride;
    }

    @ProblemFactProperty // Override from a fact collection to a single fact
    @ValueRangeProvider(id = "valueRange")
    @Override
    public List<TestdataValue> getProblemFactList() {
        return problemFactListFieldOverride;
    }

    @Override
    public List<TestdataEntity> getEntityList() {
        return entityListFieldOverride;
    }

    @Override
    public TestdataEntity getOtherEntity() {
        return otherEntityFieldOverride;
    }

}
