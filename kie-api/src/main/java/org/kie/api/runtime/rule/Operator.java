package org.kie.api.runtime.rule;

/**
 * An interface for Operator definitions.
 */
public interface Operator {

    /**
     * @return the String representation for this operator
     */
    String getOperatorString();

    /**
     * @return true if this operator instance is negated, otherwise false.
     */
    boolean isNegated();

}
