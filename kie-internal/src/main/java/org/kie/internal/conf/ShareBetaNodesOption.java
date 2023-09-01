package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * An Enum for ShareBetaNodes option.
 *
 * drools.shareBetaNodes = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum ShareBetaNodesOption implements SingleValueRuleBaseOption {

    YES(true),
    NO(false);

    /**
     * The property name for the share beta nodes option
     */
    public static final String PROPERTY_NAME = "drools.shareBetaNodes";

    public static OptionKey<SingleValueRuleBaseOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private boolean value;

    ShareBetaNodesOption( final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isShareBetaNodes() {
        return this.value;
    }

}
