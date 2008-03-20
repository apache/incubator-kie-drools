package org.drools.integrationtests;

import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;

import java.io.StringReader;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA. User: SG0521861 Date: Mar 18, 2008 Time: 1:22:50 PM To change this template use File |
 * Settings | File Templates.
 */
public class LargeRuleBaseSerializationTest extends TestCase {
    private static final int    RULE_COUNT  = 1000;

    public void testLargeRuleBase() throws Exception{
        System.out.println("Generating "+RULE_COUNT+" rules");
        StringBuilder   sb  = new StringBuilder(LargeRuleBase.getHeader());

        for (int i = 0; i < RULE_COUNT; i++) {
            sb.append(LargeRuleBase.getTemplate1("testRule"+i, i));
        }
        System.out.println("Parsing "+RULE_COUNT+" rules");
        DrlParser ps = new DrlParser();
        PackageDescr pkgDescr = ps.parse(new StringReader(sb.toString()));

        PackageBuilder pkgBuilder = new PackageBuilder();
        pkgBuilder.addPackage(pkgDescr);

        Package pkg = pkgBuilder.getPackage();
        RuleBase rb = RuleBaseFactory.newRuleBase();

        rb.addPackage(pkg);

        rb  = SerializationHelper.serializeObject(rb);
    }

}
