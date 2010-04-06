package org.drools.verifier.components;

import java.text.DateFormat;
import java.util.Date;

/**
 * 
 * @author trikkola
 *
 */
public class DateRestriction extends LiteralRestriction {

    private Date value;

    public DateRestriction(Pattern pattern) {
        super( pattern );
    }

    public void setValue(Date value) {
        this.value = value;
    }

    public Date getValue() {
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
