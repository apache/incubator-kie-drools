package org.optaplanner.core.impl.testdata.domain.pinned.chained;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;

@PlanningEntity(pinningFilter = TestdataChainedEntityPinningFilter.class)
public class TestdataPinnedChainedEntity extends TestdataObject implements TestdataChainedObject {

    public static EntityDescriptor<TestdataPinnedChainedSolution> buildEntityDescriptor() {
        return TestdataPinnedChainedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataPinnedChainedEntity.class);
    }

    public static GenuineVariableDescriptor<TestdataPinnedChainedSolution> buildVariableDescriptorForChainedObject() {
        return buildEntityDescriptor().getGenuineVariableDescriptor("chainedObject");
    }

    private TestdataChainedObject chainedObject;
    private boolean pinned;

    public TestdataPinnedChainedEntity() {
    }

    public TestdataPinnedChainedEntity(String code) {
        super(code);
    }

    public TestdataPinnedChainedEntity(String code, TestdataChainedObject chainedObject) {
        this(code);
        this.chainedObject = chainedObject;
    }

    public TestdataPinnedChainedEntity(String code, TestdataChainedObject chainedObject, boolean pinned) {
        this(code, chainedObject);
        this.pinned = pinned;
    }

    @PlanningVariable(valueRangeProviderRefs = { "chainedAnchorRange",
            "chainedEntityRange" }, graphType = PlanningVariableGraphType.CHAINED)
    public TestdataChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
