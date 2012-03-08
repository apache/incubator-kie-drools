package org.drools.planner.core.testdata.domain;

public class TestdataObject {

    private String code;

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
