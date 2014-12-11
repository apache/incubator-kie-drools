package org.optaplanner.core.impl.testdata.domain.chained.rich;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataRichChainedEntity extends TestdataObject implements TestdataRichChainedObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataRichChainedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataRichChainedEntity.class);
    }

    private TestdataRichChainedObject chainedObject;

    // Shadow variables
    private TestdataRichChainedEntity nextEntity;
    private TestdataRichChainedAnchor anchor;

    public TestdataRichChainedEntity() {
    }

    public TestdataRichChainedEntity(String code) {
        super(code);
    }

    public TestdataRichChainedEntity(String code, TestdataRichChainedObject chainedObject) {
        this(code);
        this.chainedObject = chainedObject;
    }

    @PlanningVariable(valueRangeProviderRefs = {"chainedAnchorRange", "chainedEntityRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    public TestdataRichChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataRichChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    public TestdataRichChainedEntity getNextEntity() {
        return nextEntity;
    }

    public void setNextEntity(TestdataRichChainedEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    @AnchorShadowVariable(sourceVariableName = "chainedObject")
    public TestdataRichChainedAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(TestdataRichChainedAnchor anchor) {
        this.anchor = anchor;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
