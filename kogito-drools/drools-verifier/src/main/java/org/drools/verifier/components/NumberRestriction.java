package org.drools.verifier.components;

/**
 * 
 * @author trikkola
 *
 */
public class NumberRestriction extends LiteralRestriction {

    private Number value;

    public NumberRestriction(Pattern pattern) {
        super( pattern );
    }

    public void setValue(Number number) {
        this.value = number;;
    }

    public boolean isInt() {
        return value instanceof Integer;
    }

    public Number getValue() {
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
