package org.drools.mvel.integrationtests.facts;

public class FactWithString {

    private final String stringValue;

    public FactWithString(final String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
