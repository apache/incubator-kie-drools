package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * An Enum for ShareAlphaNodes option.
 *
 * drools.shareAlphaNodes = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum ShareAlphaNodesOption implements SingleValueRuleBaseOption {

    YES(true),
    NO(false);

    /**
     * The property name for the sequential mode option
     */
    public static final String PROPERTY_NAME = "drools.shareAlphaNodes";

    public static OptionKey<ShareAlphaNodesOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private boolean value;

    ShareAlphaNodesOption( final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isShareAlphaNodes() {
        return this.value;
    }

}
