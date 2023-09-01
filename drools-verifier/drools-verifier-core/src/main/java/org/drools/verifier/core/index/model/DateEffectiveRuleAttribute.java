package org.drools.verifier.core.index.model;

import java.util.Date;

public class DateEffectiveRuleAttribute
        implements RuleAttribute {

    public static String NAME = "date-effective";

    private final int index;
    private final Date value;

    public DateEffectiveRuleAttribute(final int index,
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
