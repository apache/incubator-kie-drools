package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;

public enum PropertySpecificOption implements SingleValueRuleBuilderOption {

    DISABLED, ALLOWED, ALWAYS;

    public static final String PROPERTY_NAME = "drools.propertySpecific";

    public static OptionKey<PropertySpecificOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isAllowed() {
        return this != DISABLED;
    }

    public boolean isPropSpecific(boolean hasPropSpecAnnotation, boolean hasNotPropSpecAnnotation) {
        return (this == PropertySpecificOption.ALLOWED && hasPropSpecAnnotation ) ||
               (this == PropertySpecificOption.ALWAYS && !hasNotPropSpecAnnotation);

    }
}
