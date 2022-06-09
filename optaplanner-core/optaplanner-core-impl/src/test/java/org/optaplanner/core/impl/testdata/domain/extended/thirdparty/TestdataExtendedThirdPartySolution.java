package org.optaplanner.core.impl.testdata.domain.extended.thirdparty;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataExtendedThirdPartySolution extends TestdataThirdPartySolutionPojo {

    public static SolutionDescriptor<TestdataExtendedThirdPartySolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataExtendedThirdPartySolution.class,
                TestdataExtendedThirdPartyEntity.class);
    }

    private Object extraObject;

    private SimpleScore score;

    public TestdataExtendedThirdPartySolution() {
    }

    public TestdataExtendedThirdPartySolution(String code) {
        super(code);
    }

    public TestdataExtendedThirdPartySolution(String code, Object extraObject) {
        super(code);
        this.extraObject = extraObject;
    }

    public Object getExtraObject() {
        return extraObject;
    }

    public void setExtraObject(Object extraObject) {
        this.extraObject = extraObject;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    @ValueRangeProvider(id = "valueRange")
    @ProblemFactCollectionProperty
    public List<TestdataValue> getValueList() {
        return super.getValueList();
    }

    @Override
    @PlanningEntityCollectionProperty
    public List<TestdataThirdPartyEntityPojo> getEntityList() {
        return super.getEntityList();
    }

}
