package org.optaplanner.core.impl.testdata.domain.pinned;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity(pinningFilter = TestdataPinningFilter.class)
public class TestdataPinnedEntity extends TestdataObject {

    public static EntityDescriptor<TestdataPinnedSolution> buildEntityDescriptor() {
        return TestdataPinnedSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataPinnedEntity.class);
    }

    private TestdataValue value;
    private boolean locked;
    private boolean pinned;

    public TestdataPinnedEntity() {
    }

    public TestdataPinnedEntity(String code) {
        super(code);
    }

    public TestdataPinnedEntity(String code, boolean locked, boolean pinned) {
        this(code);
        this.locked = locked;
        this.pinned = pinned;
    }

    public TestdataPinnedEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    public TestdataPinnedEntity(String code, TestdataValue value, boolean locked, boolean pinned) {
        this(code, value);
        this.locked = locked;
        this.pinned = pinned;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @PlanningPin
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
