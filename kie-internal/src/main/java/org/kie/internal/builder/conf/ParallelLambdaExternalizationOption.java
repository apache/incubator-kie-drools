package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;

/**
 * An Enum for ParallelLambdaExternalizationOption option.
 *
 * drools.parallelLambdaExternalization = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum ParallelLambdaExternalizationOption implements SingleValueRuleBuilderOption {

    ENABLED(true),
    DISABLED(false);

    /**
     * The property name for the parallel lambda externalization
     */
    public static final String PROPERTY_NAME = "drools.parallelLambdaExternalization";

    public static OptionKey<ParallelLambdaExternalizationOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private boolean value;

    ParallelLambdaExternalizationOption(final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isLambdaExternalizationParallel() {
        return this.value;
    }

}
