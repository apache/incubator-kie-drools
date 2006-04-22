package org.drools.integrationtests;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.Sensor;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

/** 
 * This runs the integration test cases with the leaps implementation.
 * In some cases features are not supported, or their behaviour is different in leaps. In that case 
 * the test method is overridden - if this becomes common then we should refactor the common stuff out
 * into a CommonIntegrationCases suite.
 * 
 */
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
        QueryResults results = workingMemory.getQueryResults("simple query");
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

    public void testLogicalAssertions() throws Exception {
        // Not working in leaps
    }
    
    public void testLogicalAssertionsBacking() throws Exception {
        // Not working in leaps
    }

    public void testLogicalAssertionsSelfreferencing() throws Exception {
        // Not working in leaps
    }

    public void testLogicalAssertionsLoop() throws Exception {
        // Not working in leaps
    }

    public void testLogicalAssertionsNoLoop() throws Exception {
        // Not working in leaps
    }

    public void testLogicalAssertions2() throws Exception {
        // Not working in leaps
    }

    public void testLogicalAssertionsNot() throws Exception {
        // Not working in leaps
    }

    public void testLogicalAssertionsNotPingPong() throws Exception {
        // Not working in leaps
    }
    
}