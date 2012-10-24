package org.drools.conf;

/**
 * A class PermGen threshold configuration.
 */
public class PermGenThresholdOption implements SingleValueKnowledgeBaseOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the default DIALECT
     */
    public static final String PROPERTY_NAME = "drools.permgenThreshold";

    /**
     * The defualt value for this option
     */
    public static final int DEFAULT_VALUE = 90;

    /**
     * The threshold of PermGen usage (in percentage) above which the
     * engine stops JITting constraints and let them run in interpreted mode
     */
    private final int threshold;

    /**
     * Private constructor to enforce the use of the factory method
     * @param threshold
     */
    private PermGenThresholdOption( int threshold ) {
        this.threshold = threshold;
    }

    /**
     * This is a factory method for this PermGen Threshold configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param threshold the threshold value for the PermGen option
     *
     * @return the actual type safe PermGen threshold configuration.
     */
    public static PermGenThresholdOption get( int threshold ) {
        return new PermGenThresholdOption( threshold );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the threshold value for PermGen
     *
     * @return
     */
    public int getThreshold() {
        return threshold;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + threshold;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        PermGenThresholdOption other = (PermGenThresholdOption) obj;
        if ( threshold != other.threshold ) return false;
        return true;
    }
}
