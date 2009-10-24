package org.drools.verifier.components;

public class EnumRestriction extends Restriction {

    private String enumBaseGuid;
    private String enumBase;
    private String enumName;

    @Override
    public RestrictionType getRestrictionType() {
        return RestrictionType.ENUM;
    }

    public String getEnumBaseGuid() {
        return enumBaseGuid;
    }

    public void setEnumBaseGuid(String enumBaseGuid) {
        this.enumBaseGuid = enumBaseGuid;
    }

    public String getEnumBase() {
        return enumBase;
    }

    public void setEnumBase(String enumBase) {
        this.enumBase = enumBase;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    @Override
    public String toString() {
        return "QualifiedIdentifierRestrictionDescr enum: " + enumBase + "." + enumName;
    }
}
