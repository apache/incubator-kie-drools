package org.drools.runtime.rule;

/**
 * An interface for Operator definitions.
 * 
 * @author etirelli
 */
public interface Operator {

    /**
     * Returns the String representation for this operator
     * 
     * @return
     */
    public String getOperatorString();

    /**
     * Returns true if this operator instance is negated.
     * 
     * @return
     */
    public boolean isNegated();

}