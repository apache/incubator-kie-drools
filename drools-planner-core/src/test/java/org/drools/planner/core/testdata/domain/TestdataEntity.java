package org.drools.planner.core.testdata.domain;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;

import static org.mockito.Mockito.*;

@PlanningEntity
public class TestdataEntity extends TestdataObject {

    public static PlanningEntityDescriptor buildEntityDescriptor() {
        PlanningEntityDescriptor entityDescriptor = new PlanningEntityDescriptor(
                mock(SolutionDescriptor.class), TestdataEntity.class);
        entityDescriptor.processAnnotations();
        return entityDescriptor;
    }

    private TestdataValue value;

    public TestdataEntity() {
    }

    public TestdataEntity(String code) {
        super(code);
    }

    @PlanningVariable
    @ValueRange(type = ValueRangeType.UNDEFINED)
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

}
