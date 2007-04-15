package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.FactA;
import org.drools.FactB;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.Precondition;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DynamicRulesTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    
    public void testDynamicRuleAdditions() throws Exception {
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) );

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg1 = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg1 );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.setGlobal( "total",
                                 new Integer( 0 ) );

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        // Adding person in advance. There is no Person() object
        // type node in memory yet, but the rule engine is supposed
        // to handle that correctly
        final PersonInterface bob = new Person( "bob",
                                                "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.assertObject( bob );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.assertObject( cheddar );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        assertEquals( "stilton",
                      list.get( 0 ) );

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg2 = builder.getPackage();
        ruleBase.addPackage( pkg2 );

        assertEquals( 3,
                      list.size() );

        assertEquals( "stilton",
                      list.get( 0 ) );

        assertTrue( "cheddar".equals( list.get( 1 ) ) || "cheddar".equals( list.get( 2 ) ) );

        assertTrue( "stilton".equals( list.get( 1 ) ) || "stilton".equals( list.get( 2 ) ) );

        list.clear();

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg3 = builder.getPackage();
        ruleBase.addPackage( pkg3 );

        // Package 3 has a rule working on Person instances.
        // As we added person instance in advance, rule should fire now
        workingMemory.fireAllRules();

        Assert.assertEquals( "Rule from package 3 should have been fired",
                             "match Person ok",
                             bob.getStatus() );

        assertEquals( 1,
                      list.size() );

        assertEquals( bob,
                      list.get( 0 ) );

        reader = new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) );
        builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg4 = builder.getPackage();
        ruleBase.addPackage( pkg4 );

        Assert.assertEquals( "Rule from package 4 should have been fired",
                             "Who likes Stilton ok",
                             bob.getStatus() );

        assertEquals( 2,
                      list.size() );

        assertEquals( bob,
                      list.get( 1 ) );

    }

    public void testDynamicRuleRemovals() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) ) );
        final Package pkg = builder.getPackage();

        org.drools.reteoo.ReteooRuleBase reteooRuleBase = null;
        // org.drools.leaps.LeapsRuleBase leapsRuleBase = null;
        final RuleBase ruleBase = getRuleBase();
        // org.drools.reteoo.RuleBaseImpl ruleBase = new
        // org.drools.reteoo.RuleBaseImpl();
        if ( ruleBase instanceof org.drools.reteoo.ReteooRuleBase ) {
            reteooRuleBase = (org.drools.reteoo.ReteooRuleBase) ruleBase;
            // } else if ( ruleBase instanceof org.drools.leaps.LeapsRuleBase )
            // {
            // leapsRuleBase = (org.drools.leaps.LeapsRuleBase) ruleBase;
        }
        ruleBase.addPackage( pkg );
        final PackageBuilder builder2 = new PackageBuilder();
        builder2.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) ) );
        ruleBase.addPackage( builder2.getPackage() );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final PersonInterface bob = new Person( "bob",
                                                "stilton" );
        bob.setStatus( "Not evaluated" );
        workingMemory.assertObject( bob );

        final Cheese stilton1 = new Cheese( "stilton",
                                            5 );
        workingMemory.assertObject( stilton1 );

        final Cheese stilton2 = new Cheese( "stilton",
                                            3 );
        workingMemory.assertObject( stilton2 );

        final Cheese stilton3 = new Cheese( "stilton",
                                            1 );
        workingMemory.assertObject( stilton3 );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.assertObject( cheddar );
        //        
        // workingMemory.get
        //        
        // workingMemory.fireAllRules();

        assertEquals( 11,
                      workingMemory.getAgenda().getActivations().length );

        if ( reteooRuleBase != null ) {
            reteooRuleBase.removeRule( "org.drools.test",
                                       "Who likes Stilton" );
            assertEquals( 8,
                          workingMemory.getAgenda().getActivations().length );

            reteooRuleBase.removeRule( "org.drools.test",
                                       "like cheese" );

            final Cheese muzzarela = new Cheese( "muzzarela",
                                                 5 );
            workingMemory.assertObject( muzzarela );

            assertEquals( 4,
                          workingMemory.getAgenda().getActivations().length );

            reteooRuleBase.removePackage( "org.drools.test" );

            assertEquals( 0,
                          workingMemory.getAgenda().getActivations().length );
            // } else if ( leapsRuleBase != null ) {
            // leapsRuleBase.removeRule( "org.drools.test",
            // "Who likes Stilton" );
            // assertEquals( 8,
            // workingMemory.getAgenda().getActivations().length );
            //
            // leapsRuleBase.removeRule( "org.drools.test",
            // "like cheese" );
            //
            // final Cheese muzzarela = new Cheese( "muzzarela",
            // 5 );
            // workingMemory.assertObject( muzzarela );
            //
            // assertEquals( 4,
            // workingMemory.getAgenda().getActivations().length );
            //
            // leapsRuleBase.removePackage( "org.drools.test" );
            //
            // assertEquals( 0,
            // workingMemory.getAgenda().getActivations().length );
            //
        }
    }

    public void testDynamicRuleRemovalsUnusedWorkingMemory() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic2.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic4.drl" ) ) );
        final Package pkg = builder.getPackage();

        org.drools.reteoo.ReteooRuleBase reteooRuleBase = null;
        // org.drools.leaps.LeapsRuleBase leapsRuleBase = null;
        final RuleBase ruleBase = getRuleBase();
        // org.drools.reteoo.RuleBaseImpl ruleBase = new
        // org.drools.reteoo.RuleBaseImpl();
        if ( ruleBase instanceof org.drools.reteoo.ReteooRuleBase ) {
            reteooRuleBase = (org.drools.reteoo.ReteooRuleBase) ruleBase;
            // } else if ( ruleBase instanceof org.drools.leaps.LeapsRuleBase )
            // {
            // leapsRuleBase = (org.drools.leaps.LeapsRuleBase) ruleBase;
        }
        ruleBase.addPackage( pkg );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        if ( reteooRuleBase != null ) {
            assertEquals( 1,
                          reteooRuleBase.getPackages().length );
            assertEquals( 4,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removeRule( "org.drools.test",
                                       "Who likes Stilton" );
            assertEquals( 3,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removeRule( "org.drools.test",
                                       "like cheese" );
            assertEquals( 2,
                          reteooRuleBase.getPackages()[0].getRules().length );

            reteooRuleBase.removePackage( "org.drools.test" );
            assertEquals( 0,
                          reteooRuleBase.getPackages().length );
            // } else if ( leapsRuleBase != null ) {
            // assertEquals( 1,
            // leapsRuleBase.getPackages().length );
            // assertEquals( 4,
            // leapsRuleBase.getPackages()[0].getRules().length );
            //
            // leapsRuleBase.removeRule( "org.drools.test",
            // "Who likes Stilton" );
            // assertEquals( 3,
            // leapsRuleBase.getPackages()[0].getRules().length );
            //
            // leapsRuleBase.removeRule( "org.drools.test",
            // "like cheese" );
            // assertEquals( 2,
            // leapsRuleBase.getPackages()[0].getRules().length );
            //
            // leapsRuleBase.removePackage( "org.drools.test" );
            // assertEquals( 0,
            // leapsRuleBase.getPackages().length );
        }
    }
    
    public void testDynamicFunction() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction1.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        workingMemory.assertObject( stilton );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 0 ) );

        // Check a function can be removed from a package.
        // Once removed any efforts to use it should throw an Exception
        pkg.removeFunction( "addFive" );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        workingMemory.assertObject( cheddar );

        try {
            workingMemory.fireAllRules();
            fail( "Function should have been removed and NoClassDefFoundError thrown from the Consequence" );
        } catch ( final NoClassDefFoundError e ) {
        }

        // Check a new function can be added to replace an old function
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction2.drl" ) ) );

        ruleBase.addPackage( builder.getPackage() );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        workingMemory.assertObject( brie );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 6 ),
                      list.get( 1 ) );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicFunction3.drl" ) ) );

        ruleBase.addPackage( builder.getPackage() );

        final Cheese feta = new Cheese( "feta",
                                        5 );
        workingMemory.assertObject( feta );

        workingMemory.fireAllRules();

        assertEquals( new Integer( 5 ),
                      list.get( 2 ) );
    }
    
    public void testRemovePackage() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RemovePackage.drl" ) ) );

        final RuleBase ruleBase = getRuleBase();
        final String packageName = builder.getPackage().getName();
        ruleBase.addPackage( builder.getPackage() );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        workingMemory.assertObject( new Precondition( "genericcode",
                                                      "genericvalue" ) );
        workingMemory.fireAllRules();

        final RuleBase ruleBaseWM = workingMemory.getRuleBase();
        ruleBaseWM.removePackage( packageName );
        final PackageBuilder builder1 = new PackageBuilder();
        builder1.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_RemovePackage.drl" ) ) );
        ruleBaseWM.addPackage( builder1.getPackage() );
        workingMemory.fireAllRules();

        ruleBaseWM.removePackage( packageName );
        ruleBaseWM.addPackage( builder1.getPackage() );

        ruleBaseWM.removePackage( packageName );
        ruleBaseWM.addPackage( builder1.getPackage() );
    }
    
    public void testDynamicRules() throws Exception {
        final RuleBase ruleBase = getRuleBase();
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        final Cheese a = new Cheese( "stilton",
                                     10 );
        final Cheese b = new Cheese( "stilton",
                                     15 );
        final Cheese c = new Cheese( "stilton",
                                     20 );
        workingMemory.assertObject( a );
        workingMemory.assertObject( b );
        workingMemory.assertObject( c );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRules.drl" ) ) );
        final Package pkg = builder.getPackage();
        ruleBase.addPackage( pkg );

        workingMemory.fireAllRules();
    }

    public void testDynamicRules2() throws Exception {
        final RuleBase ruleBase = getRuleBase();
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        // Assert some simple facts
        final FactA a = new FactA( "hello",
                                   new Integer( 1 ),
                                   new Float( 3.14 ) );
        final FactB b = new FactB( "hello",
                                   new Integer( 2 ),
                                   new Float( 6.28 ) );
        workingMemory.assertObject( a );
        workingMemory.assertObject( b );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DynamicRules2.drl" ) ) );
        final Package pkg = builder.getPackage();
        ruleBase.addPackage( pkg );

        workingMemory.fireAllRules();
    }    

}
