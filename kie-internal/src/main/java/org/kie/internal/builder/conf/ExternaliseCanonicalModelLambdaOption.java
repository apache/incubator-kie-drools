package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;

/**
 * An Enum for ExternaliseCanonicalModelLambda option.
 *
 * drools.externaliseCanonicalModelLambda = &lt;true|false&gt;
 *
 * DEFAULT = false
 */
public enum ExternaliseCanonicalModelLambdaOption implements SingleValueRuleBuilderOption {

    ENABLED(true),
    DISABLED(false);

    /**
     * The property name for the sequential mode option
     */
    public static final String PROPERTY_NAME = "drools.externaliseCanonicalModelLambda";

    public static OptionKey<ExternaliseCanonicalModelLambdaOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private boolean value;

    ExternaliseCanonicalModelLambdaOption(final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isCanonicalModelLambdaExternalized() {
        return this.value;
    }

}
