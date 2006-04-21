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
 * @version $Id: RuleBaseFactory.java,v 1.4 2005/11/25 02:35:33 mproctor Exp $
 */
public class ConwayRuleBaseFactory {

	private static ConwayRuleBaseFactory ourInstance = new ConwayRuleBaseFactory ();

	private RuleBase ruleBase;

	public static ConwayRuleBaseFactory getInstance() {
		return ourInstance;
	}

	private ConwayRuleBaseFactory () {
		try {
			/**
			 * Please note that this is the "low level" rule assembly API.
			 */
			// private static RuleBase readRule() throws Exception {
			// read in the source
			Reader source = new InputStreamReader(ConwayRuleBaseFactory.class
					.getResourceAsStream("/conway.drl"));

			// optionally read in the DSL (if you are using it).
			Reader dsl = new InputStreamReader(ConwayRuleBaseFactory.class
					.getResourceAsStream("/conway.dsl"));

			// Use package builder to build up a rule package.
			// An alternative lower level class called "DrlParser" can also be
			// used...

			PackageBuilder builder = new PackageBuilder();

			// this wil parse and compile in one step
			// NOTE: There are 2 methods here, the one argument one is for
			// normal DRL.
			// builder.addPackageFromDrl( source );

			// Use the following instead of above if you are using a DSL:
			builder.addPackageFromDrl(source, dsl);

			// get the compiled package (which is serializable)
			Package pkg = builder.getPackage();

			// add the package to a rulebase (deploy the rule package).
			ruleBase = org.drools.RuleBaseFactory.newRuleBase();
			ruleBase.addPackage(pkg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static RuleBase getRuleBase() {
		return ourInstance.ruleBase;
	}
}
