package org.drools.verifier.core.index.model;

import java.util.Date;

public class DateExpiresRuleAttribute
        implements RuleAttribute {

    public static String NAME = "date-expires";

    private final int index;
    private final Date value;

    public DateExpiresRuleAttribute(final int index,
                                    final Date value) {
        this.index = index;
        this.value = value;
    }

    public Date getValue() {
        return value;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
