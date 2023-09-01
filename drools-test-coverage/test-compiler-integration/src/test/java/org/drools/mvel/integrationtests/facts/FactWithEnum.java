package org.drools.mvel.integrationtests.facts;

public class FactWithEnum {

    private final AnEnum enumValue;

    public FactWithEnum(final AnEnum enumValue) {
        this.enumValue = enumValue;
    }

    public AnEnum getEnumValue() {
        return enumValue;
    }
}
