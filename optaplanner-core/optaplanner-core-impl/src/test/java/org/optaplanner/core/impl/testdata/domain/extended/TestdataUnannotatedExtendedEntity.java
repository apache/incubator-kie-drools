package org.optaplanner.core.impl.testdata.domain.extended;

import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataUnannotatedExtendedEntity extends TestdataEntity {

    private Object extraObject;

    public TestdataUnannotatedExtendedEntity() {
    }

    public TestdataUnannotatedExtendedEntity(String code) {
        super(code);
    }

    public TestdataUnannotatedExtendedEntity(String code, TestdataValue value) {
        super(code, value);
    }

    public TestdataUnannotatedExtendedEntity(String code, TestdataValue value, Object extraObject) {
        super(code, value);
        this.extraObject = extraObject;
    }

    public Object getExtraObject() {
        return extraObject;
    }

    public void setExtraObject(Object extraObject) {
        this.extraObject = extraObject;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
