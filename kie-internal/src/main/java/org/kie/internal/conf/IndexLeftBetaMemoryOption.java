package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * An Enum for indexLeftBetaMemory option.
 *
 * drools.indexLeftBetaMemory = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum IndexLeftBetaMemoryOption implements SingleValueRuleBaseOption {

    YES(true),
    NO(false);

    /**
     * The property name for the share beta nodes option
     */
    public static final String PROPERTY_NAME = "drools.indexLeftBetaMemory";

    public static OptionKey<IndexLeftBetaMemoryOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private boolean value;

    IndexLeftBetaMemoryOption( final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isIndexLeftBetaMemory() {
        return this.value;
    }

}
