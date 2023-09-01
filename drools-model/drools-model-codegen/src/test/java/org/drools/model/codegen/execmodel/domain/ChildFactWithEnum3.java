package org.drools.model.codegen.execmodel.domain;

public class ChildFactWithEnum3 {

    private final int id;
    private final int parentId;
    private final EnumFact1 enumValue;

    public ChildFactWithEnum3(final int id, final int parentId, final EnumFact1 enumValue) {
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

    public EnumFact1 getEnumValue() {
        return enumValue;
    }
}
