package org.kie.api.conf;

/**
 * An Enum for MBeans Enabled option.
 *
 * kie.mbeans = &lt;enabled|disabled&gt;
 *
 * DEFAULT = false
 */
public enum MBeansOption implements SingleValueKieBaseOption {

    ENABLED(true),
    DISABLED(false);

    /**
     * The property name for the mbeans option
     */
    public static final String PROPERTY_NAME = "kie.mbeans";

    public static OptionKey<MBeansOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private boolean value;

    MBeansOption( final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isEnabled() {
        return this.value;
    }

    public static boolean isEnabled(final String value) {
        if( value == null || value.trim().length() == 0 ) {
            return false;
        } else if ( "ENABLED".equalsIgnoreCase( value ) ) {
            return true;
        } else if ( "DISABLED".equalsIgnoreCase( value ) ) {
            return false;
        } else {
            throw new IllegalArgumentException( "Illegal enum value '" + value + "' for MBeans option. Should be either 'enabled' or 'disabled'" );
        }
    }


}
