package org.optaplanner.persistence.jackson.impl.testdata.domain;

import org.optaplanner.core.impl.testdata.util.CodeAssertable;

public abstract class JacksonTestdataObject implements CodeAssertable {

    protected String code;

    public JacksonTestdataObject() {
    }

    public JacksonTestdataObject(String code) {
        this.code = code;
    }

    @Override
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
