package org.drools.builder.conf;

public enum PropertySpecificOption implements SingleValueKnowledgeBuilderOption {

    DISABLED, ALLOWED, ALWAYS;

    public static final String PROPERTY_NAME = "drools.propertySpecific";

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
