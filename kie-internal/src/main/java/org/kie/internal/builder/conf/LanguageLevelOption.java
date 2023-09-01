package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;

/**
 * A class for the language level configuration.
 */
public enum LanguageLevelOption implements SingleValueRuleBuilderOption {

    DRL5(false), DRL6(false), DRL6_STRICT(true);

    private final boolean useJavaAnnotations;

    /**
     * The property name for the language level
     */
    public static final String PROPERTY_NAME = "drools.lang.level";

    public static OptionKey<LanguageLevelOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    LanguageLevelOption(boolean useJavaAnnotations) {
        this.useJavaAnnotations = useJavaAnnotations;
    }

    public boolean useJavaAnnotations() {
        return useJavaAnnotations;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

}
