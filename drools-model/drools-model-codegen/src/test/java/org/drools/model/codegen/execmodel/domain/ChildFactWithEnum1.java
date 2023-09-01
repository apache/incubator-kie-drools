package org.drools.model.codegen.execmodel.domain;

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

    public int getParentId() {
        return parentId;
    }

    public EnumFact1 getEnumValue() {
        return enumValue;
    }

    public String getEnumValueFromInterface() {
        switch (enumValue) {
            case FIRST: return InterfaceAsEnum.FIRST;
            case SECOND: return InterfaceAsEnum.SECOND;
            case THIRD: return InterfaceAsEnum.THIRD;
            case FOURTH: return InterfaceAsEnum.FOURTH;
        }
        throw new RuntimeException( "UNKNOWN" );
    }
}
