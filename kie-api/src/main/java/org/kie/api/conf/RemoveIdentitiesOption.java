package org.kie.api.conf;


/**
 * An Enum for Remove Identities option.
 *
 * drools.removeIdentities = &lt;true|false&gt;
 *
 * DEFAULT = false
 */
public enum RemoveIdentitiesOption implements SingleValueRuleBaseOption {

    YES(true),
    NO(false);

    /**
     * The property name for the remove identities option
     */
    public static final String PROPERTY_NAME = "drools.removeIdentities";

    public static OptionKey KEY = new OptionKey(TYPE, PROPERTY_NAME);

    private boolean value;

    RemoveIdentitiesOption( final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isRemoveIdentities() {
        return this.value;
    }

}
