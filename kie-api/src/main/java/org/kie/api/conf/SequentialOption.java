package org.kie.api.conf;

/**
 * An Enum for Sequential option.
 *
 * drools.sequential = &lt;true|false&gt;
 *
 * DEFAULT = false
 */
public enum SequentialOption implements SingleValueRuleBaseOption {

    YES(true),
    NO(false);

    /**
     * The property name for the sequential mode option
     */
    public static final String PROPERTY_NAME = "drools.sequential";

    public static OptionKey<SequentialOption> KEY = new OptionKey(TYPE, PROPERTY_NAME);

    private boolean value;

    SequentialOption( final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isSequential() {
        return this.value;
    }

    public static SequentialOption determineSequential(String option) {
        if ( YES.name().equalsIgnoreCase(option) || "true".equalsIgnoreCase(option) ) {
            return YES;
        } else if ( NO.name().equalsIgnoreCase(option) || "false".equalsIgnoreCase(option) ) {
            return NO;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + option + "' for DeclarativeAgendaOption" );
    }
}
