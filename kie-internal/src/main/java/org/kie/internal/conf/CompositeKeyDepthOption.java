package org.kie.internal.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueRuleBaseOption;

/**
 * A class for the composite key depth configuration.
 */
public class CompositeKeyDepthOption implements SingleValueRuleBaseOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the default DIALECT
     */
    public static final String PROPERTY_NAME = "drools.compositeKeyDepth";

    public static OptionKey<CompositeKeyDepthOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    /**
     * dialect name
     */
    private final int depth;

    /**
     * Private constructor to enforce the use of the factory method
     * @param depth
     */
    private CompositeKeyDepthOption( int depth ) {
        this.depth = depth;
    }

    /**
     * This is a factory method for this CompositeKeyDepth configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param depth the composite key depth value
     *
     * @return the actual type safe CompositeKeyDepth configuration.
     */
    public static CompositeKeyDepthOption get( int depth ) {
        return new CompositeKeyDepthOption( depth );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the depth value for composite key indexing
     *
     * @return
     */
    public int getDepth() {
        return depth;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + depth;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) { return true; }
        if ( obj == null ) { return false; }
        if ( getClass() != obj.getClass() ) { return false; }
        CompositeKeyDepthOption other = (CompositeKeyDepthOption) obj;
        if ( depth != other.depth ) {
            return false;
        }
        return true;
    }

}
