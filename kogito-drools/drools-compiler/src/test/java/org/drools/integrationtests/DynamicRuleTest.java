package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

public class DynamicRuleTest extends TestCase {
        public void testDrl() throws Exception {
            Reader reader = new InputStreamReader( getClass().getResourceAsStream( "dynamic-rule-test.drl" ) );
            DrlParser parser = new DrlParser();
            PackageDescr packageDescr = parser.parse( reader );
            
            PackageBuilder builder = new PackageBuilder();
            builder.addPackage( packageDescr );
            Package pkg = builder.getPackage();
            
            org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
            ruleBase.addPackage( pkg );
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
            
            Cheese stilton = new Cheese("stilton", 5);
            workingMemory.assertObject( stilton );
            workingMemory.fireAllRules();
        }
}
