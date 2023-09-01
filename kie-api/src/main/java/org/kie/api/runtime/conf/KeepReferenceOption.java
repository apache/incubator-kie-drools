package org.kie.api.runtime.conf;

import org.kie.api.conf.OptionKey;

/**
 * Option to configure if the KieBase should retain a reference to the
 * KieSession or not. The default is YES, i.e., the reference is retained.
 */
public enum KeepReferenceOption implements SingleValueKieSessionOption {

    YES(true),
    NO(false);

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the keep reference configuration
     */
    public static final String PROPERTY_NAME = "drools.keepReference";

    public static OptionKey<KeepReferenceOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private final boolean keepReference;

    /**
     * Private constructor to enforce the use of the factory method
     * @param keepReference
     */
    KeepReferenceOption( final boolean keepReference ) {
        this.keepReference = keepReference;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isKeepReference() {
        return keepReference;
    }

}
