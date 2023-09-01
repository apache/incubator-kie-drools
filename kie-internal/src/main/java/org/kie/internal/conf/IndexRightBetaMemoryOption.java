package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * An Enum for indexRightBetaMemory option.
 *
 * drools.indexRightBetaMemory = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum IndexRightBetaMemoryOption implements SingleValueRuleBaseOption {

    YES(true),
    NO(false);

    /**
     * The property name for the share beta nodes option
     */
    public static final String PROPERTY_NAME = "drools.indexRightBetaMemory";

    public static OptionKey<IndexRightBetaMemoryOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private boolean value;

    IndexRightBetaMemoryOption( final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isIndexRightBetaMemory() {
        return this.value;
    }

}
