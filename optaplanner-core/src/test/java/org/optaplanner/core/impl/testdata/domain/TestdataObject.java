package org.optaplanner.core.impl.testdata.domain;

import org.optaplanner.core.impl.testdata.util.CodeAssertable;

public class TestdataObject implements CodeAssertable {

    protected String code;

    public TestdataObject() {
    }

    public TestdataObject(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

}
