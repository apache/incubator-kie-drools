package org.drools.conf;

/**
 * An Enum for Index Precedence option.
 *
 * drools.indexPrecedence = &lt;pattern|equality&gt;
 *
 * When creating indexes gives precedence to the equality constraints (default)
 * or to the first indexable constraint in the pattern.
 *
 * DEFAULT = equality
 */
public enum IndexPrecedenceOption implements SingleValueKnowledgeBaseOption {

    PATTERN_ORDER("pattern"),
    EQUALITY_PRIORITY("equality");

    /**
     * The property name for the index precedence option
     */
    public static final String PROPERTY_NAME = "drools.indexPrecedence";

    private String             string;

    IndexPrecedenceOption(String mode) {
        this.string = mode;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public String getValue() {
        return string;
    }

    public String toString() {
        return "IndexPrecedenceOption( "+string+ " )";
    }

    public String toExternalForm() {
        return this.string;
    }

    public static IndexPrecedenceOption determineIndexPrecedence(String mode) {
        if ( PATTERN_ORDER.getValue().equalsIgnoreCase( mode ) ) {
            return PATTERN_ORDER;
        } else if ( EQUALITY_PRIORITY.getValue().equalsIgnoreCase( mode ) ) {
            return EQUALITY_PRIORITY;
        }
        throw new IllegalArgumentException( "Illegal enum value '" + mode + "' for IndexPrecedence" );
    }

}
