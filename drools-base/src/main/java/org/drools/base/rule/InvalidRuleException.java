package org.drools.base.rule;

import org.drools.base.definitions.rule.impl.RuleImpl;

/**
 * Indicates an error regarding the semantic validity of a rule.
 */
public class InvalidRuleException extends RuleConstructionException {
    private static final long serialVersionUID = 510l;
    /** The invalid rule. */
    private RuleImpl rule;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param rule
     *            The invalid <code>Rule</code>.
     */
    public InvalidRuleException(final RuleImpl rule) {
        super();
        this.rule = rule;
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     * 
     * @param message
     * @param rule
     */
    public InvalidRuleException(final String message,
                                final RuleImpl rule) {
        super( message );
        this.rule = rule;
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     * 
     * @param message
     * @param rule
     */
    public InvalidRuleException(final String message,
                                final RuleImpl rule,
                                final Throwable cause) {
        super( message,
               cause );
        this.rule = rule;
    }

    /**
     * Retrieve the invalid <code>Rule</code>.
     * 
     * @return The invalid <code>Rule</code>.
     */
    public RuleImpl getRule() {
        return this.rule;
    }
}
