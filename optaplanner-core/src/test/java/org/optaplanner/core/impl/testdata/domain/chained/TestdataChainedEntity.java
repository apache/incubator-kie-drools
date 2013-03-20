package org.optaplanner.core.impl.testdata.domain.chained;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.ValueRangeType;
import org.optaplanner.core.api.domain.value.ValueRanges;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataChainedEntity extends TestdataObject implements TestdataChainedObject {

    public static PlanningEntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        return solutionDescriptor.getPlanningEntityDescriptor(TestdataChainedEntity.class);
    }

    private TestdataChainedObject chainedObject;

    public TestdataChainedEntity() {
    }

    public TestdataChainedEntity(String code) {
        super(code);
    }

    public TestdataChainedEntity(String code, TestdataChainedObject chainedObject) {
        this(code);
        this.chainedObject = chainedObject;
    }

    @PlanningVariable(chained = true)
    @ValueRanges({
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "chainedAnchorList"),
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "chainedEntityList",
                    excludeUninitializedPlanningEntity = true)})
    public TestdataChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
