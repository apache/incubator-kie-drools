package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

/**
 *
 * @author Toni Rikkola
 */
public class Constraint extends PatternComponent
    implements
    Cause {

    private boolean patternIsNot;
    private String  fieldPath;
    private String  fieldName;
    private int     lineNumber;

    public Constraint(Pattern pattern) {
        super( pattern );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.CONSTRAINT;
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
        return "Constraint field name: " + fieldName;
    }

    public void setFieldPath(String path) {
        this.fieldPath = path;
    }

    public String getFieldPath() {
        return fieldPath;
    }
}
