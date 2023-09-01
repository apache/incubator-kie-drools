package org.drools.mvel.integrationtests.facts;

public class ChildFact3WithEnum {

    private final int id;
    private final int parentId;
    private final AnEnum enumValue;

    public ChildFact3WithEnum(final int id, final int parentId, final AnEnum enumValue) {
        this.id = id;
        this.parentId = parentId;
        this.enumValue = enumValue;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public AnEnum getEnumValue() {
        return enumValue;
    }
}
