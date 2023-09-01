package org.kie.api.conf;


/**
 * An option to define if a KieBase should be mutable or not.
 * By default mutability (incremental compilation) is allowed but for performances reasonds
 * it is strongly recommended to set this option to "disabled" if you don't need it.
 *
 * drools.kieBaseMutability = &lt;allowed|disabled&gt;
 *
 * DEFAULT = allowed
 */
public enum KieBaseMutabilityOption implements SingleValueKieBaseOption {

    ALLOWED,
    DISABLED;

    /**
     * The property name for the sequential mode option
     */
    public static final String PROPERTY_NAME = "drools.kieBaseMutability";

    public static OptionKey<KieBaseMutabilityOption> KEY = new OptionKey(TYPE, PROPERTY_NAME);

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public static KieBaseMutabilityOption determineMutability( String option ) {
        if ( ALLOWED.name().equalsIgnoreCase(option) ) {
            return ALLOWED;
        } else if ( DISABLED.name().equalsIgnoreCase( option ) ) {
            return DISABLED;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + option + "' for KieBaseMutabilityOption" );
    }

    public boolean isMutabilityEnabled() {
        return this == ALLOWED;
    }
}
