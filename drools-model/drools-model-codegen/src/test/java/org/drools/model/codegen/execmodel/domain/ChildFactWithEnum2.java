package org.drools.model.codegen.execmodel.domain;

public class ChildFactWithEnum2 {

    private final int id;
    private final int parentId;
    private final EnumFact2 enumValue;

    public ChildFactWithEnum2(final int id, final int parentId, final EnumFact2 enumValue) {
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

    public EnumFact2 getEnumValue() {
        return enumValue;
    }
}
