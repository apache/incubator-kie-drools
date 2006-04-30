/**
 *
 */
package org.drools.examples.conway;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

/**
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class ConwayRuleBaseFactory {

    private static final ConwayRuleBaseFactory ourInstance = new ConwayRuleBaseFactory();

    private RuleBase                     ruleBase;

    public static ConwayRuleBaseFactory getInstance() {
        return ConwayRuleBaseFactory.ourInstance;
    }

    private ConwayRuleBaseFactory() {
        try {
            /**
             * Please note that this is the "low level" rule assembly API.
             */
            // private static RuleBase readRule() throws Exception {
            // read in the source
            final Reader source = new InputStreamReader( ConwayRuleBaseFactory.class.getResourceAsStream( "/conway/conway.drl" ) );

            // optionally read in the DSL (if you are using it).
            final Reader dsl = new InputStreamReader( ConwayRuleBaseFactory.class.getResourceAsStream( "/conway/conway.dsl" ) );

            // Use package builder to build up a rule package.
            // An alternative lower level class called "DrlParser" can also be
            // used...

            final PackageBuilder builder = new PackageBuilder();

            // this wil parse and compile in one step
            // NOTE: There are 2 methods here, the one argument one is for
            // normal DRL.
            // builder.addPackageFromDrl( source );

            // Use the following instead of above if you are using a DSL:
            builder.addPackageFromDrl( source,
                                       dsl );

            // get the compiled package (which is serializable)
            final Package pkg = builder.getPackage();

            // add the package to a rulebase (deploy the rule package).
            this.ruleBase = org.drools.RuleBaseFactory.newRuleBase();
            this.ruleBase.addPackage( pkg );
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    public static RuleBase getRuleBase() {
        return ConwayRuleBaseFactory.ourInstance.ruleBase;
    }
}
