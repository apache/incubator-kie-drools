package org.drools.planner.core.testdata.domain;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;

import static org.mockito.Mockito.mock;

@PlanningEntity
public class TestdataChainedEntity extends TestdataObject implements TestdataChainedObject {

    public static PlanningEntityDescriptor buildEntityDescriptor() {
        PlanningEntityDescriptor entityDescriptor = new PlanningEntityDescriptor(
                mock(SolutionDescriptor.class), TestdataChainedEntity.class);
        entityDescriptor.processAnnotations();
        return entityDescriptor;
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
    @ValueRange(type = ValueRangeType.UNDEFINED)
    public TestdataChainedObject getChainedObject() {
        return chainedObject;
    }

    public void setChainedObject(TestdataChainedObject chainedObject) {
        this.chainedObject = chainedObject;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public TestdataChainedEntity clone() {
        TestdataChainedEntity clone = new TestdataChainedEntity();
        clone.code = code;
        clone.chainedObject = chainedObject;
        return clone;
    }

}
