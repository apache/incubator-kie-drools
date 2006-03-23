package org.drools.rule;

/**
 * This exception is thrown when an invalid package (ie one that has errors)
 * it attempted to be added to a RuleBase.
 * The package and builder should be interrogated to show the specific errors.
 * 
 * @author Michael Neale
 */
public class InvalidRulePackage extends RuntimeException {
    
    private static final long serialVersionUID = 7244017661666655680L;

    public InvalidRulePackage(String summary) {
        super( summary );
    }
    
}
