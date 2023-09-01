package org.kie.api.runtime.conf;

import org.kie.api.conf.OptionKey;

/**
 * A class for the session clock configuration.
 */
public class ClockTypeOption implements SingleValueKieSessionOption {

    private static final long serialVersionUID = 510l;

    public static final ClockTypeOption PSEUDO = ClockTypeOption.get("pseudo");
    public static final ClockTypeOption REALTIME = ClockTypeOption.get("realtime");

    /**
     * The property name for the clock type configuration
     */
    public static final String PROPERTY_NAME = "drools.clockType";

    public static OptionKey<ClockTypeOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    /**
     * clock type
     */
    private final String clockType;

    /**
     * Private constructor to enforce the use of the factory method
     * @param clockType
     */
    private ClockTypeOption( String clockType ) {
        this.clockType = clockType;
    }

    /**
     * This is a factory method for this Clock Type configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param clockType the identifier for the clock type
     *
     * @return the actual type safe default clock type configuration.
     */
    public static ClockTypeOption get( String clockType ) {
        return new ClockTypeOption( clockType );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * @return the configured clock type
     */
    public String getClockType() {
        return clockType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (( clockType == null) ? 0 :  clockType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) { return true; }
        if ( obj == null ) { return false; }
        if ( getClass() != obj.getClass() ) { return false; }
        ClockTypeOption other = (ClockTypeOption) obj;
        if (  clockType == null ) {
            if ( other.clockType != null ) {
                return false;
            }
        } else if ( ! clockType.equals( other.clockType ) ) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ClockTypeOption( "+ clockType +" )";
    }
}
