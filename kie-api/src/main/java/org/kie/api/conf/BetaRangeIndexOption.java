package org.kie.api.conf;

/**
 * <p>
 * An enum to enable beta node range index option.
 * </p>
 *
 * <pre>
 * drools.betaNodeRangeIndexEnabled = &lt;true|false&gt;
 * </pre>
 *
 * <b>DEFAULT = false</b>
 *
 */
public enum BetaRangeIndexOption implements SingleValueRuleBaseOption {

    ENABLED(true),
    DISABLED(false);

    /**
     * The property name for beta node range index option
     */
    public static final String PROPERTY_NAME = "drools.betaNodeRangeIndexEnabled";

    public static OptionKey<BetaRangeIndexOption> KEY = new OptionKey(TYPE, PROPERTY_NAME);

    private boolean value;

    BetaRangeIndexOption(final boolean value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isBetaRangeIndexEnabled() {
        return this.value;
    }

    public static BetaRangeIndexOption determineBetaRangeIndex(String option) {
        if (ENABLED.name().equalsIgnoreCase(option) || "true".equalsIgnoreCase(option)) {
            return ENABLED;
        } else if (DISABLED.name().equalsIgnoreCase(option) || "false".equalsIgnoreCase(option)) {
            return DISABLED;
        }
        throw new IllegalArgumentException("Illegal enum value '" + option + "' for BetaRangeIndexOption");
    }

}
