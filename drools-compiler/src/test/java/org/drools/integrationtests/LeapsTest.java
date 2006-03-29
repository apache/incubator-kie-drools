package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.List;

import org.drools.Cheese;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

/** This runs the integration test cases with the leaps implementation */
public class LeapsTest extends IntegrationCases {

    protected RuleBase getRuleBase() throws Exception {
        return new org.drools.leaps.RuleBaseImpl();
    }

	/**
	 * Leaps query requires fireAll run before any probing can be done. this
	 * test mirrors one in IntegrationCases.java with addition of call to
	 * workingMemory.fireAll to facilitate query execution
	 */    
    public void testQuery() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simple_query_test.drl" ) ) );
        Package pkg = builder.getPackage();
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        Cheese stilton = new Cheese("stinky", 5);
        workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();
        List results = workingMemory.getQueryResults( "simple query" );
        assertEquals(1, results.size());        
    }
    
    public void testAgendaGroups() throws Exception {
        //not implemented yet
    }

    public void testEvalException() throws Exception {
        //not implemented yet
    }

    public void testPredicateException() throws Exception {
        //not implemented yet
    }

    public void testReturnValueException() throws Exception {
        //not implemented yet
    }

    public void testDurationWithNoLoop() {
        //not implemented yet
    }

    public void testNoLoop() throws Exception {
        //not implemented yet
    }

}
