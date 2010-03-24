package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierFieldAccessDescr extends RuleComponent {

    private String fieldName;
    private String argument;

    public VerifierFieldAccessDescr(VerifierRule rule) {
        super( rule );
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.FIELD_ACCESSOR;
    }
}
