package org.drools.ancompiler;

public class ChildFactWithEnum1 {

    private final int id;
    private final int parentId;
    private final EnumFact1 enumValue;

    public ChildFactWithEnum1(final int id, final int parentId, final EnumFact1 enumValue) {
        this.id = id;
        this.parentId = parentId;
        this.enumValue = enumValue;
    }

    public int getId() {
        return id;
    }

    public EnumFact1 getEnumValue() {
        return enumValue;
    }

}
