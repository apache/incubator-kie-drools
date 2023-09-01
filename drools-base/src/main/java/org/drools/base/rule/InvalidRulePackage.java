package org.drools.base.rule;

/**
 * This exception is thrown when an invalid package (ie one that has errors)
 * it attempted to be added to a RuleBase.
 * The package and builder should be interrogated to show the specific errors.
 */
public class InvalidRulePackage extends RuntimeException {

    private static final long serialVersionUID = 510l;

    public InvalidRulePackage(final String summary) {
        super( summary );
    }

}
