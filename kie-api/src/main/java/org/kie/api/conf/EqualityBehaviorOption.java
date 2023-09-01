package org.kie.api.conf;


/**
 * An Enum for EqualityBehavior option.
 *
 * drools.equalityBehavior = &lt;identity|equality&gt;
 *
 * DEFAULT = identity
 */
public enum EqualityBehaviorOption implements SingleValueRuleBaseOption {

    IDENTITY,
    EQUALITY;

    /**
     * The property name for the sequential mode option
     */
    public static final String PROPERTY_NAME = "drools.equalityBehavior";

    public static final OptionKey KEY = new OptionKey(TYPE, PROPERTY_NAME);

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public static EqualityBehaviorOption determineEqualityBehavior(String option) {
        if ( IDENTITY.name().equalsIgnoreCase(option) ) {
            return IDENTITY;
        } else if ( EQUALITY.name().equalsIgnoreCase( option ) ) {
            return EQUALITY;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + option + "' for EqualityBehaviorOption" );
    }
}
