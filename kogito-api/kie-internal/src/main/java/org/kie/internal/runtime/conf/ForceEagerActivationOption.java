package org.kie.internal.runtime.conf;

import org.kie.api.runtime.conf.SingleValueKieSessionOption;

/**
 * Option to force evaluation and then activation of rules annotated with @Eager.
 */
public enum ForceEagerActivationOption implements SingleValueKieSessionOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = "drools.forceEagerActivation";

    private final boolean forceEagerActivation;

    private ForceEagerActivationOption( final boolean forceEagerActivation ) {
        this.forceEagerActivation = forceEagerActivation;
    }

    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isForceEagerActivation() {
        return forceEagerActivation;
    }
}
