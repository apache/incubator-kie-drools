package org.drools.runtime.conf;

/**
 * A class for the timer job factory manager configuration.
 */
public class TimerJobFactoryOption implements SingleValueKnowledgeSessionOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the timer job factory manager configuration
     */
    public static final String PROPERTY_NAME = "drools.timerJobFactory";

    /**
     * Belie System Type
     */
    private final String timerJobType;

    /**
     * Private constructor to enforce the use of the factory method
     * @param timerJobType
     */
    private TimerJobFactoryOption( String timerJobType) {
        this.timerJobType = timerJobType;
    }

    /**
     * This is a factory method for this timer job factory manager configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param timerJobType  the identifier for the belie system
     *
     * @return the actual type safe timer job factory manager configuration.
     */
    public static TimerJobFactoryOption get( String timerJobType ) {
        return new TimerJobFactoryOption( timerJobType );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the configured timer job factory manager
     *
     * @return
     */
    public String getTimerJobType() {
        return timerJobType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (( timerJobType == null) ? 0 :  timerJobType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TimerJobFactoryOption other = (TimerJobFactoryOption) obj;
        if (  timerJobType == null ) {
            if ( other.timerJobType != null ) return false;
        } else if ( ! timerJobType.equals( other.timerJobType ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "TimerJobFactoryOption( "+ timerJobType +" )";
    }
}
