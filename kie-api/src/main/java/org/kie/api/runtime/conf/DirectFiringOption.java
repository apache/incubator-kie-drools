package org.kie.api.runtime.conf;

import org.kie.api.conf.OptionKey;

/**
 * An option to define if the KieSession should directly firing consequences bypassing the agenda.
 * By default this option is disabled. Enabling this will bring a performance improvement at the cost
 * of being no longer able to use salience, no-loop and other features that affect rules' precedence.
 *
 * drools.directFiring = &lt;true|false&gt;
 *
 * DEFAULT = false
 */
public enum DirectFiringOption implements SingleValueRuleRuntimeOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the direct firing configuration
     */
    public static final String PROPERTY_NAME = "drools.directFiring";

    public static OptionKey<DirectFiringOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private final boolean directFiring;

    /**
     * Private constructor to enforce the use of the factory method
     * @param directFiring
     */
    DirectFiringOption( final boolean directFiring ) {
        this.directFiring = directFiring;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isDirectFiring() {
        return directFiring;
    }

}