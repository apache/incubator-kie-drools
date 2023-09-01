package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * A class for the max threads configuration.
 */
public class MaxThreadsOption implements SingleValueRuleBaseOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the max threads
     */
    public static final String PROPERTY_NAME = "drools.maxThreads";

    public static OptionKey<MaxThreadsOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    /**
     * max threads
     */
    private final int maxThreads;

    /**
     * Private constructor to enforce the use of the factory method
     * @param maxThreads
     */
    private MaxThreadsOption( int maxThreads ) {
        this.maxThreads = maxThreads;
    }

    /**
     * This is a factory method for this Max Threads configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param threshold the maximum number of threads for partition evaluation
     *
     * @return the actual type safe max threads configuration.
     */
    public static MaxThreadsOption get( int threshold ) {
        return new MaxThreadsOption( threshold );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the maximum number of threads for partition evaluation
     *
     * @return
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + maxThreads;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) { return true; }
        if ( obj == null ) { return false; }
        if ( getClass() != obj.getClass() ) { return false; }
        MaxThreadsOption other = (MaxThreadsOption) obj;
        if ( maxThreads != other.maxThreads ) {
            return false;
        }
        return true;
    }

}
