package org.drools;

public class AssertedObject {
    private String value;

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public AssertedObject(final String value) {
        this.value = value;
    }

    public AssertedObject() {
    }

}