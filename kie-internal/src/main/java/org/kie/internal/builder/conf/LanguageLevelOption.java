package org.kie.internal.builder.conf;

/**
 * A class for the language level configuration.
 */
public enum LanguageLevelOption implements SingleValueKnowledgeBuilderOption {

    DRL5, DRL6;

    /**
     * The property name for the language level
     */
    public static final String PROPERTY_NAME = "drools.lang.level";

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
}
