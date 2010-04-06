package org.drools.verifier.components;

/**
 * 
 * @author trikkola
 *
 */
public class BooleanRestriction extends LiteralRestriction {

    private Boolean value;

    public BooleanRestriction(Pattern pattern) {
        super( pattern );
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return value.toString();
    }

    @Override
    public String getValueType() {
        return value.getClass().getName();
    }

}
