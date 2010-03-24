package org.drools.verifier;

import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.VerifierRule;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierComponentMockFactory {

    public static RulePackage createPackage1() {
        RulePackage rulePackage = new RulePackage();

        rulePackage.setName( "testPackage1" );

        return rulePackage;
    }

    public static VerifierRule createRule1() {
        return createRule( 1 );
    }

    public static Pattern createPattern1() {
        return createPattern( 1 );
    }

    public static VerifierRule createRule2() {
        return createRule( 2 );
    }

    public static Pattern createPattern2() {
        return createPattern( 2 );
    }

    public static VerifierRule createRule(int i) {
        VerifierRule rule = new VerifierRule( createPackage1() );

        rule.setName( "testRule" + i );

        return rule;
    }

    public static Pattern createPattern(int i) {
        Pattern pattern = new Pattern( createRule( i ) );

        pattern.setName( "testPattern" + 1 );

        return pattern;
    }
}
