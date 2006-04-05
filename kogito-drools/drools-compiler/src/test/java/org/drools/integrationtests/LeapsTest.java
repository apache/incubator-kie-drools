package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
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
        builder.addPackageFromDrl(new InputStreamReader(getClass()
                .getResourceAsStream("simple_query_test.drl")));
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage(pkg);
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese stilton = new Cheese("stinky", 5);
        workingMemory.assertObject(stilton);
        workingMemory.fireAllRules();// <=== the only difference from the base test case
        List results = workingMemory.getQueryResults("simple query");
        assertEquals(1, results.size());
    }

    /**
     * leaps does not create activations upfront hence its inability to apply
     * auto-focus predicate in the same way as reteoo does. activations in
     * reteoo sense created in the order rules would fire based what used to be
     * called conflict resolution.
     * 
     * So, while agenda groups feature works it mirrors reteoo behaviour up to
     * the point where auto-focus comes into play. At this point leaps and
     * reteoo are different at which point auto-focus should "fire".
     * 
     * the other problem that relates to the lack of activations before rules
     * start firing is that agenda group is removed from focus stack when agenda
     * group is empty. This also affects module / focus behaviour
     */
    public void testAgendaGroups() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new InputStreamReader(getClass()
                .getResourceAsStream("test_AgendaGroups.drl")));
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage(pkg);
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal("list", list);

        Cheese brie = new Cheese("brie", 12);
        workingMemory.assertObject(brie);

        workingMemory.fireAllRules();

        assertEquals(3, list.size());

        assertEquals("MAIN", list.get(0)); // salience 10
        assertEquals("group3", list.get(1)); // salience 5. set auto focus to
        // group 3
        // no group 3 activations at this point, pop it, next activation that
        // can fire is MAIN
        assertEquals("MAIN", list.get(2));
        // assertEquals( "group2", list.get( 3 ) );
        // assertEquals( "group4", list.get( 4 ) );
        // assertEquals( "group1", list.get( 5 ) );
        // assertEquals( "group3", list.get( 6 ) );
        // assertEquals( "group1", list.get( 7 ) );

        workingMemory.setFocus("group2");
        workingMemory.fireAllRules();

        assertEquals(4, list.size());
        assertEquals("group2", list.get(3));
    }

    /**
     * exception test are leaps specific due to the fact that left hand side of
     * the rule is not being evaluated until fireAllRules is called. Otherwise
     * the test cases are exactly the same
     */
    public void testEvalException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new InputStreamReader(getClass()
                .getResourceAsStream("test_EvalException.drl")));
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage(pkg);
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese brie = new Cheese("brie", 12);

        try {
            workingMemory.assertObject(brie);
            workingMemory.fireAllRules(); // <=== the only difference from the base test case
            fail("Should throw an Exception from the Eval");
        } catch (Exception e) {
            assertEquals("this should throw an exception", e.getCause()
                    .getMessage());
        }
    }

    public void testPredicateException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new InputStreamReader(getClass()
                .getResourceAsStream("test_PredicateException.drl")));
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage(pkg);
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese brie = new Cheese("brie", 12);

        try {
            workingMemory.assertObject(brie);
            workingMemory.fireAllRules(); // <=== the only difference from the base test case
            fail("Should throw an Exception from the Predicate");
        } catch (Exception e) {
            assertEquals("this should throw an exception", e.getCause()
                    .getMessage());
        }
    }

    public void testReturnValueException() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(new InputStreamReader(getClass()
                .getResourceAsStream("test_ReturnValueException.drl")));
        Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage(pkg);
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Cheese brie = new Cheese("brie", 12);

        try {
            workingMemory.assertObject(brie);
            workingMemory.fireAllRules(); // <=== the only difference from the base test case
            fail("Should throw an Exception from the ReturnValue");
        } catch (Exception e) {
            assertEquals("this should throw an exception", e.getCause()
                    .getMessage());
        }
    }

}
