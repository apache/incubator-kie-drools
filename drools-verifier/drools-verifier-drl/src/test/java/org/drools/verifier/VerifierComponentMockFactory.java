package org.drools.verifier;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.VerifierRule;

import java.util.HashMap;

public class VerifierComponentMockFactory {

    public static RulePackage createPackage1() {
        PackageDescr descr = new PackageDescr("testPackage1");
        RulePackage rulePackage = new RulePackage(descr);

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
        VerifierRule rule = new VerifierRule( new PackageDescr("testPackage1"), createPackage1(), new HashMap<String, Object>());

        rule.setName( "testRule" + i );

        return rule;
    }

    public static Pattern createPattern(int i) {
        Pattern pattern = new Pattern( new PatternDescr("objectType" + i, "testPattern" + i ),  createRule( i ) );
        pattern.setObjectTypePath( "objectType" + i );

        pattern.setName( "testPattern" + i );

        return pattern;
    }
}
