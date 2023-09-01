package org.drools.base.rule;

import org.drools.base.definitions.rule.impl.RuleImpl;

/**
 * Validity exception indicating that a <code>Rule</code> does not contain a
 * <code>Consequence</code>s.
 */
public class NoConsequenceException extends InvalidRuleException {
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    private static final long serialVersionUID = 510l;

    /**
     * Construct.
     * 
     * @param rule
     *            The invalid <code>Rule</code>.
     */
    public NoConsequenceException(final RuleImpl rule) {
        super( rule );
    }
}
