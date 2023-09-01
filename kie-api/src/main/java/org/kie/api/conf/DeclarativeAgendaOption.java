package org.kie.api.conf;

/**
 * <p>
 * An enum to enable Declarative Agenda option.
 * This is an experimental feature.
 * </p>
 *
 * <pre>
 * drools.declarativeAgenda = &lt;true|false&gt;
 * </pre>
 *
 * <b>DEFAULT = false.</b>
 *
 */
public enum DeclarativeAgendaOption implements SingleValueRuleBaseOption {

    ENABLED(true), DISABLED(false);

    /**
     * The property name for the L and R Unlinking option
     */
    public static final String PROPERTY_NAME = "drools.declarativeAgendaEnabled";

    public static OptionKey KEY = new OptionKey(TYPE, PROPERTY_NAME);

    private boolean value;

    DeclarativeAgendaOption(final boolean value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isDeclarativeAgendaEnabled() {
        return this.value;
    }

    public static DeclarativeAgendaOption determineDeclarativeAgenda(String option) {
        if ( ENABLED.name().equalsIgnoreCase(option) || "true".equalsIgnoreCase(option) ) {
            return ENABLED;
        } else if ( DISABLED.name().equalsIgnoreCase(option) || "false".equalsIgnoreCase(option) ) {
            return DISABLED;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + option + "' for DeclarativeAgendaOption" );
    }
}
