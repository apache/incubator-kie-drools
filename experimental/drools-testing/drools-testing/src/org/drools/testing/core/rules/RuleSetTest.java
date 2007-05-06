package org.drools.testing.core.rules;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.testing.core.rules.model.Account;

public class RuleSetTest {
	
	public static final void main(final String[] args) {
		RuleSetTest test = new RuleSetTest();
        test.testRules();
    }

	/**
	 * Loads a package and creates a rulebase
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static RuleBase readRule(String file) throws Exception {
        //read in the source
        Reader reader = new InputStreamReader( RuleSetTest.class.getResourceAsStream( file ) );
        DrlParser parser = new DrlParser();
        PackageDescr packageDescr = parser.parse( reader );

        //pre build the package
        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        Package pkg = builder.getPackage();

        //add the package to a rulebase
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        return ruleBase;
    }
	
	public void testRules() {
        try {
            String file = "test.drl";
            RuleBase ruleBase = readRule( file );
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
            Account acc  = new Account();
            workingMemory.assertObject(acc);
            workingMemory.fireAllRules();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
