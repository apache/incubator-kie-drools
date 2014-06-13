package org.optaplanner.core.impl.testdata.domain.chained.mappedby;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataMappedByChainedEntity extends TestdataObject implements TestdataMappedByChainedObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataMappedByChainedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataMappedByChainedEntity.class);
    }

    private TestdataMappedByChainedObject chainedObject;

    // Shadow variables
    private TestdataMappedByChainedEntity nextEntity;

    public TestdataMappedByChainedEntity() {
    }

    public TestdataMappedByChainedEntity(String code) {
        super(code);
    }

    public TestdataMappedByChainedEntity(String code, TestdataMappedByChainedObject chainedObject) {
        this(code);
        this.chainedObject = chainedObject;
    }

    @PlanningVariable(valueRangeProviderRefs = {"chainedAnchorRange", "chainedEntityRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    public TestdataMappedByChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataMappedByChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    public TestdataMappedByChainedEntity getNextEntity() {
        return nextEntity;
    }

    public void setNextEntity(TestdataMappedByChainedEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
