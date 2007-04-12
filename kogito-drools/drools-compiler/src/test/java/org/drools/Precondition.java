package org.drools;

public class Precondition {
    private String code;
    private String value;

    public Precondition() {

    }

    public Precondition(final String code,
                        final String value) {
        super();
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

}