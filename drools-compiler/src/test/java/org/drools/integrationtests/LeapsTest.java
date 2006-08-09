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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.PackageDescr;
import org.drools.leaps.LeapsRuleBase;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.xml.XmlDumper;

/** 
 * This runs the integration test cases with the leaps implementation.
 * In some cases features are not supported, or their behaviour is different in leaps. In that case 
 * the test method is overridden - if this becomes common then we should refactor the common stuff out
 * into a CommonIntegrationCases suite.
 * 
 */
public class LeapsTest extends IntegrationCases {

    protected RuleBase getRuleBase() throws Exception {
        return RuleBaseFactory.newRuleBase( RuleBase.LEAPS );
    }
    
    

    public void testFactTemplate() throws Exception {
		// TODO FIXME !		
	}



	/**
     * Leaps query requires fireAll run before any probing can be done. this
     * test mirrors one in IntegrationCases.java with addition of call to
     * workingMemory.fireAll to facilitate query execution
     */
    public void testQuery() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simple_query_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final Cheese stilton = new Cheese( "stinky",
                                     5 );
        workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();// <=== the only difference from the base test case
        final QueryResults results = workingMemory.getQueryResults( "simple query" );
        assertEquals( 1,
                      results.size() );
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

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_AgendaGroups.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                  12 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      list.size() );

        assertEquals( "MAIN",
                      list.get( 0 ) ); // salience 10
        assertEquals( "group3",
                      list.get( 1 ) ); // salience 5. set auto focus to
        // group 3
        // no group 3 activations at this point, pop it, next activation that
        // can fire is MAIN
        assertEquals( "MAIN",
                      list.get( 2 ) );
        // assertEquals( "group2", list.get( 3 ) );
        // assertEquals( "group4", list.get( 4 ) );
        // assertEquals( "group1", list.get( 5 ) );
        // assertEquals( "group3", list.get( 6 ) );
        // assertEquals( "group1", list.get( 7 ) );

        workingMemory.setFocus( "group2" );
        workingMemory.fireAllRules();

        assertEquals( 4,
                      list.size() );
        assertEquals( "group2",
                      list.get( 3 ) );
    }

    // the problem here is that rete does conflict resolution after all
    // activations
    // are generated and takes rules salience as a first sorting criteria
    // and only then uses fact recency while leaps uses fact recency (and other
    // possible
    // criteria) first and rule based after
    public void testLogicalAssertions2() throws Exception {
        // Not working in leaps
    }

    // while Xor group behaviour is supported by leaps certain functionality is no
    // due to the lazy nature of leaps and the fact that it does not accumulate 
    // activations before firing them we can not do counts on activation groups and 
    // agenda groups as in base integration test
    public void testXorGroups() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ActivationGroups.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                  12 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 3,
                      list.size() );
        assertTrue( "rule0",
                    list.contains( "rule0" ) );
        assertTrue( "rule1",
                    list.contains( "rule1" ) );
        assertTrue( "rule2",
                    list.contains( "rule2" ) );
    }

    /**
     * this test is replicated here due to the fact that leaps
     * does not create activations before fireAll.
     * 
     * so the only difference is in presence of fireAll () statement
     */
    public void testDynamicRuleRemovals() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass( ).getResourceAsStream( "test_Dynamic3.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass( ).getResourceAsStream( "test_Dynamic4.drl" ) ) );
        final Package pkg = builder.getPackage( );

        org.drools.leaps.LeapsRuleBase leapsRuleBase = null;
        final RuleBase ruleBase = getRuleBase( );
        leapsRuleBase = (org.drools.leaps.LeapsRuleBase) ruleBase;
        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory( );

        final List list = new ArrayList( );
        workingMemory.setGlobal( "list", list );

        final PersonInterface bob = new Person( "bob", "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.assertObject( bob );

        final Cheese stilton1 = new Cheese( "stilton", 5 );
        workingMemory.assertObject( stilton1 );

        final Cheese stilton2 = new Cheese( "stilton", 3 );
        workingMemory.assertObject( stilton2 );

        final Cheese stilton3 = new Cheese( "stilton", 1 );
        workingMemory.assertObject( stilton3 );

        final Cheese cheddar = new Cheese( "cheddar", 5 );
        workingMemory.assertObject( cheddar );
        //        
        //        workingMemory.get
        //        
        workingMemory.fireAllRules( );

        assertEquals( 11, workingMemory.getAgenda( ).getActivations( ).length );

        leapsRuleBase.removeRule( "org.drools.test", "Who likes Stilton" );
        assertEquals( 8, workingMemory.getAgenda( ).getActivations( ).length );

        leapsRuleBase.removeRule( "org.drools.test", "like cheese" );

        final Cheese muzzarela = new Cheese( "muzzarela", 5 );
        workingMemory.assertObject( muzzarela );

        assertEquals( 4, workingMemory.getAgenda( ).getActivations( ).length );

        leapsRuleBase.removePackage( "org.drools.test" );

        assertEquals( 0, workingMemory.getAgenda( ).getActivations( ).length );
    }
    
    public void testSerializable() throws Exception {       
        
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = builder.getPackage();

        assertEquals( 0,
                      builder.getErrors().length );

        RuleBase ruleBase = getRuleBase();//RuleBaseFactory.newRuleBase();

        ruleBase.addPackage( pkg );

        final byte[] ast = serializeOut( ruleBase );
        ruleBase = (RuleBase) serializeIn( ast );
        final Rule[] rules = ruleBase.getPackages()[0].getRules();
        assertEquals( 4,
                      rules.length );

        assertEquals( "match Person 1",
                      rules[0].getName() );
        assertEquals( "match Person 2",
                      rules[1].getName() );
        assertEquals( "match Person 3",
                      rules[2].getName() );
        assertEquals( "match Integer",
                      rules[3].getName() );        
        
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        workingMemory.setGlobal( "list", new ArrayList() );
        
        workingMemory.assertObject( new Integer(5) );        
        
        final byte[] wm = serializeOut( workingMemory );

        workingMemory = ruleBase.newWorkingMemory( new ByteArrayInputStream( wm ) );
        
        assertEquals( 1, workingMemory.getObjects().size() );
        assertEquals( new Integer( 5 ) , workingMemory.getObjects().get(0) );        
        
        workingMemory.fireAllRules();
        
        List list = ( List ) workingMemory.getGlobal( "list" );
        
        assertEquals( 1, list.size() );
        assertEquals( new Integer( 4 ), list.get( 0 ) );
        
        assertEquals( 2, workingMemory.getObjects().size() );
        assertEquals( new Integer( 5 ) , workingMemory.getObjects().get(0) );
        assertEquals( "help" , workingMemory.getObjects().get(1) );        
    }
    
    
    public void testActivationGroups() throws Exception {
        // @todo for some reason this is b0rked?
    }
    
    /**
     * this test is replicated here due to the fact that leaps
     * does not create activations before fireAll.
     * 
     * Lack of activations' pool makes "auto-focus" functionality unvailable. 
     * While outcome of the rules is different it's still testing dumpers and 
     * gets consitent outcome with it.
     */
    public void testDumpers() throws Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr pkg = parser.parse( new InputStreamReader( getClass().getResourceAsStream( "test_Dumpers.drl" ) ) );        
        
        PackageBuilder builder = new PackageBuilder();
        builder.addPackage(pkg );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                  12 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 2, 
                      list.size() );
        assertEquals( "MAIN", 
                      list.get( 0 ) );
        assertEquals( "3 1", 
                      list.get( 1 ) );
        
        final DrlDumper drlDumper = new DrlDumper();
        final String drlResult = drlDumper.dump( pkg );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( drlResult ) );
        
        ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        workingMemory = ruleBase.newWorkingMemory();

        list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 2, 
                      list.size() );
        assertEquals( "MAIN", 
                      list.get( 0 ) );
        assertEquals( "3 1", 
                      list.get( 1 ) );
        
        final XmlDumper xmlDumper = new XmlDumper();
        final String xmlResult = xmlDumper.dump( pkg );
        builder = new PackageBuilder();
        builder.addPackageFromXml( new StringReader( xmlResult ) );
        
        ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        workingMemory = ruleBase.newWorkingMemory();

        list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( 2, 
                      list.size() );
        assertEquals( "MAIN", 
                      list.get( 0 ) );
        assertEquals( "3 1", 
                      list.get( 1 ) );
    }
    
    
}
