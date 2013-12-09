package org.optaplanner.core.impl.testdata.domain.extended;

import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataExtendedEntity extends TestdataEntity {

    private Object extraObject;

    public TestdataExtendedEntity() {
    }

    public TestdataExtendedEntity(String code) {
        super(code);
    }

    public TestdataExtendedEntity(String code, TestdataValue value) {
        super(code, value);
    }

    public TestdataExtendedEntity(String code, TestdataValue value, Object extraObject) {
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
