package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public class Constraint extends PatternComponent
    implements
    Cause {

    private boolean patternIsNot;
    private String  fieldGuid;
    private String  fieldName;
    private int     lineNumber;

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.CONSTRAINT;
    }

    public CauseType getCauseType() {
        return CauseType.CONSTRAINT;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean isPatternIsNot() {
        return patternIsNot;
    }

    public void setPatternIsNot(boolean patternIsNot) {
        this.patternIsNot = patternIsNot;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "Constraint id: " + getGuid() + " field name: " + fieldName;
    }

    public void setFieldGuid(String guid) {
        this.fieldGuid = guid;
    }

    public String getFieldGuid() {
        return fieldGuid;
    }
}
