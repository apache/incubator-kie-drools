package org.kie.api.runtime.conf;

import org.kie.api.conf.OptionKey;

/**
 * An option to define if 'accumulate' propagates its result even when its accumulate function result is 'null'.
 * For example, min(), max(), ave() returns 'null' when no fact matches the Pattern.
 *
 * drools.accumulateNullPropagation = &lt;true|false&gt;
 *
 * DEFAULT = false
 */
public enum AccumulateNullPropagationOption implements SingleValueRuleRuntimeOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the accumulate null propagation configuration
     */
    public static final String PROPERTY_NAME = "drools.accumulateNullPropagation";

    public static OptionKey<AccumulateNullPropagationOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private final boolean accumulateNullPropagation;

    /**
     * Private constructor to enforce the use of the factory method
     * @param accumulateNullPropagation
     */
    AccumulateNullPropagationOption(final boolean accumulateNullPropagation) {
        this.accumulateNullPropagation = accumulateNullPropagation;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isAccumulateNullPropagation() {
        return accumulateNullPropagation;
    }

}
