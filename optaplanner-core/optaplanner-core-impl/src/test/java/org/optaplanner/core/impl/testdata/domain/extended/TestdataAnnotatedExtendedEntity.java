package org.optaplanner.core.impl.testdata.domain.extended;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataAnnotatedExtendedEntity extends TestdataEntity {

    private TestdataValue subValue;

    public TestdataAnnotatedExtendedEntity() {
    }

    public TestdataAnnotatedExtendedEntity(String code) {
        super(code);
    }

    public TestdataAnnotatedExtendedEntity(String code, TestdataValue value) {
        super(code, value);
    }

    @PlanningVariable(valueRangeProviderRefs = "subValueRange")
    public TestdataValue getSubValue() {
        return subValue;
    }

    public void setSubValue(TestdataValue subValue) {
        this.subValue = subValue;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
