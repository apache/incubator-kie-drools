package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class QualifiedIdentifierRestriction extends Restriction {

    private String variableGuid;
    private String variableName;
    private String variablePath;

    @Override
    public RestrictionType getRestrictionType() {
        return RestrictionType.QUALIFIED_IDENTIFIER;
    }

    public String getValueAsString() {
        return variablePath + "." + variableName;
    }

    public String getVariableGuid() {
        return variableGuid;
    }

    public void setVariableGuid(String variableGuid) {
        this.variableGuid = variableGuid;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariablePath() {
        return variablePath;
    }

    public void setVariablePath(String variablePath) {
        this.variablePath = variablePath;
    }

    @Override
    public String toString() {
        return "QualifiedIdentifierRestriction name: " + variableName + variablePath;
    }

}
