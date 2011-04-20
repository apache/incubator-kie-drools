package org.drools.reteoo;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBaseConfiguration;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.integrationtests.LargeRuleBase;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.rule.Rule;

/**
 * Created by IntelliJ IDEA. User: SG0521861 Date: Mar 20, 2008 Time: 2:36:47 PM To change this template use File |
 * Settings | File Templates.
 */
public class ReteooBuilderPerformanceTest {
    private static final int    RULE_COUNT  = Integer.parseInt(System.getProperty("rule.count", "1000"));
    private static final int    RETEBUILDER_COUNT  = Integer.parseInt(System.getProperty("retebuilder.count", "1"));

    @Test
    public void testReteBuilder() throws DroolsParserException {
        addRules(generatePackage(RULE_COUNT));
    }

    private static void addRules(Package pkg) {
        ReteooBuilder[]  reteBuilders   = getReteBuilders(RETEBUILDER_COUNT);

        System.out.println("Adding rules to ReteBuilder");
        long    start   = System.currentTimeMillis();
        for (ReteooBuilder reteBuilder : reteBuilders) {
            for (Rule rule : pkg.getRules())
                reteBuilder.addRule(rule);
        }
        System.out.println("Added "+RULE_COUNT+" rules to each ReteBuilder's in "+
                           format(System.currentTimeMillis()-start));
    }

    private static ReteooBuilder[] getReteBuilders(int count) {
        System.out.println("Creating "+count+" ReteBuilder's");
        ReteooBuilder[]  reteBuilders   = new ReteooBuilder[count];
        RuleBaseConfiguration conf = new RuleBaseConfiguration();

        for (int i = 0; i < reteBuilders.length; i++) {
            reteBuilders[i] = new ReteooBuilder(new ReteooRuleBase( conf ));
        }
        return reteBuilders;
    }

    private static Package generatePackage(int ruleCount) throws DroolsParserException {
        StringReader    reader  = new StringReader(generateRules(ruleCount));
        
        System.out.println("Generating packages");
        PackageBuilder pkgBuilder = new PackageBuilder();
        try {
            pkgBuilder.addPackageFromDrl( reader );
        } catch ( IOException e ) { 
            fail( "Unable to parse rules\n" + e.getMessage());
        }

        if ( pkgBuilder.hasErrors() ) {
            fail( pkgBuilder.getErrors().toString() );
        }

        return pkgBuilder.getPackage();
    }

    private static String generateRules(int ruleCount) {
        System.out.println("Generating "+ruleCount+" rules");
        StringBuilder   sb  = new StringBuilder(LargeRuleBase.getHeader());

        for (int i = 1; i <= ruleCount; i++) {
            sb.append(LargeRuleBase.getTemplate1("testrule"+i, i));
        }
        return sb.toString();
    }

    private static final int    MILLIS_IN_SECOND   = 1000;
    private static final int    MILLIS_IN_MINUTE    = MILLIS_IN_SECOND*60;
    private static final int    MILLIS_IN_HOUR      = MILLIS_IN_MINUTE*60;

    private static String format(long time) {
        StringBuilder   sb  = new StringBuilder();

        sb.append(time/MILLIS_IN_HOUR).append(':');
        time = time % MILLIS_IN_HOUR;
        sb.append(time/MILLIS_IN_MINUTE).append(':');
        time = time % MILLIS_IN_MINUTE;
        sb.append(time*1.0/MILLIS_IN_SECOND);
        return sb.toString();
    }
}
