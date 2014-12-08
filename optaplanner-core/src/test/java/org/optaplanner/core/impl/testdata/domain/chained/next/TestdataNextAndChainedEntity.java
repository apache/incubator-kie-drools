package org.optaplanner.core.impl.testdata.domain.chained.next;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataNextAndChainedEntity extends TestdataObject implements TestdataNextAndChainedObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataNextAndChainedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataNextAndChainedEntity.class);
    }

    private TestdataNextAndChainedObject chainedObject;

    // Shadow variables
    private TestdataNextAndChainedEntity nextEntity;

    public TestdataNextAndChainedEntity() {
    }

    public TestdataNextAndChainedEntity(String code) {
        super(code);
    }

    public TestdataNextAndChainedEntity(String code, TestdataNextAndChainedObject chainedObject) {
        this(code);
        this.chainedObject = chainedObject;
    }

    @PlanningVariable(valueRangeProviderRefs = {"chainedAnchorRange", "chainedEntityRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    public TestdataNextAndChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataNextAndChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    public TestdataNextAndChainedEntity getNextEntity() {
        return nextEntity;
    }

    public void setNextEntity(TestdataNextAndChainedEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
