package org.optaplanner.core.impl.testdata.domain.extended;

import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class TestdataUnannotatedExtendedSolution extends TestdataSolution {

    private Object extraObject;

    public TestdataUnannotatedExtendedSolution() {
    }

    public TestdataUnannotatedExtendedSolution(String code) {
        super(code);
    }

    public TestdataUnannotatedExtendedSolution(String code, Object extraObject) {
        super(code);
        this.extraObject = extraObject;
    }

    public TestdataUnannotatedExtendedSolution(TestdataSolution other) {
        super(other.getCode());
        setValueList(other.getValueList());
        setEntityList(other.getEntityList());
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
