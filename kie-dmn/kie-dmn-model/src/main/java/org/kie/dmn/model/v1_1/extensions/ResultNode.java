package org.kie.dmn.model.v1_1.extensions;

import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

public class ResultNode extends DMNModelInstrumentedBase {
    private ValueType computed;
    private ValueType expected;
    private Boolean errorResult = Boolean.FALSE;
    private String name;
    private String type;
    private String cast;

    public ValueType getExpected() {
        return expected;
    }

    public void setExpected(ValueType expected) {
        this.expected = expected;
    }

    public Boolean getErrorResult() {
        return errorResult;
    }

    public void setErrorResult(Boolean errorResult) {
        this.errorResult = errorResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public ValueType getComputed() {
        return computed;
    }

    public void setComputed(ValueType computed) {
        this.computed = computed;
    }
}
