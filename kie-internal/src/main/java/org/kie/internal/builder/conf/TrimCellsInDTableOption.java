package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * An option to disable trimming of spaces for values in decision tables
 *
 * drools.trimCellsInDTable = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum TrimCellsInDTableOption implements SingleValueRuleBuilderOption, SingleValueRuleBaseOption {

    ENABLED(true),
    DISABLED(false);

    /**
     * The property name for the enabling/disabling trim of cells values
     */
    public static final String PROPERTY_NAME = "drools.trimCellsInDTable";

    public static OptionKey<TrimCellsInDTableOption> KEY = new OptionKey<>(SingleValueRuleBuilderOption.TYPE, PROPERTY_NAME);


    private boolean value;

    TrimCellsInDTableOption( final boolean value ) {
        this.value = value;
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isTrimCellsInDTable() {
        return this.value;
    }

    @Override
    public String type() {
        return SingleValueRuleBuilderOption.super.type();
    }
}
