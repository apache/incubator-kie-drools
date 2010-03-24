package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class EnumRestriction extends Restriction {

    private String enumBasePath;
    private String enumBase;
    private String enumName;

    public EnumRestriction(Pattern pattern) {
        super( pattern );
    }

    @Override
    public RestrictionType getRestrictionType() {
        return RestrictionType.ENUM;
    }

    public String getEnumBasePath() {
        return enumBasePath;
    }

    public void setEnumBasePath(String enumBasePath) {
        this.enumBasePath = enumBasePath;
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
