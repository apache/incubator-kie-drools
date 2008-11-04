package org.drools.integrationtests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.Order;
import org.drools.OrderItem;
import org.drools.Person;
import org.drools.PersonInterface;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SpecialString;
import org.drools.State;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.FactHandle;

public class FirstOrderLogicTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    public void testCollect() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Collect.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        StatefulSession wm = ruleBase.newStatefulSession();
        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "stilton",
                               7 ) );
        wm.insert( new Cheese( "stilton",
                               8 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );
        wm = SerializationHelper.getSerialisedStatefulSession( wm );
        results = (List) wm.getGlobal( "results" );
        
        wm.insert( new Cheese( "provolone",
                               20 ) );
        wm.insert( new Person( "Bob",
                               "stilton" ) );
        wm.insert( new Person( "Mark",
                               "provolone" ) );
        wm = SerializationHelper.getSerialisedStatefulSession( wm );
        results = (List) wm.getGlobal( "results" );

        wm.fireAllRules();

        wm = SerializationHelper.getSerialisedStatefulSession( wm );
        results = (List) wm.getGlobal( "results" );
        
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 3,
                             ((Collection) results.get( 0 )).size() );
        Assert.assertEquals( ArrayList.class.getName(),
                             results.get( 0 ).getClass().getName() );
    }

    public void testCollectNodeSharing() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_collectNodeSharing.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        StatefulSession workingMemory = ruleBase.newStatefulSession();

        List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        workingMemory = SerializationHelper.getSerialisedStatefulSession( workingMemory );
        results = (List) workingMemory.getGlobal( "results" );
        
        workingMemory.insert( new Cheese( "stilton",
                                          10 ) );
        workingMemory = SerializationHelper.getSerialisedStatefulSession( workingMemory );
        results = (List) workingMemory.getGlobal( "results" );
        
        workingMemory.insert( new Cheese( "brie",
                                          15 ) );

        workingMemory.fireAllRules();

        workingMemory = SerializationHelper.getSerialisedStatefulSession( workingMemory );
        results = (List) workingMemory.getGlobal( "results" );
        
        assertEquals( 1,
                      results.size() );

        assertEquals( 2,
                      ((List) results.get( 0 )).size() );
    }

    public void testCollectModify() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Collect.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        StatefulSession wm = ruleBase.newStatefulSession();
        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};
        final Person bob = new Person( "Bob",
                                       "stilton" );

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }
        final FactHandle bobHandle = wm.insert( bob );

        // ---------------- 1st scenario
        int fireCount = 0;
        wm.fireAllRules();
        Assert.assertEquals( ++fireCount,
                             results.size() );
        Assert.assertEquals( 3,
                             ((Collection) results.get( fireCount - 1 )).size() );
        Assert.assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        
        wm.fireAllRules();

        Assert.assertEquals( ++fireCount,
                             results.size() );
        Assert.assertEquals( 3,
                             ((Collection) results.get( fireCount - 1 )).size() );
        Assert.assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 3rd scenario
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        Assert.assertEquals( fireCount,
                             results.size() );

        // ---------------- 4th scenario
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint
        Assert.assertEquals( fireCount,
                             results.size() );
    }

    public void testCollectResultConstraints() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CollectResultConstraints.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        StatefulSession wm = ruleBase.newStatefulSession();
        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm = SerializationHelper.getSerialisedStatefulSession( wm );
        results = (List) wm.getGlobal( "results" );
        
        wm.fireAllRules();

        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 1,
                             ((Collection) results.get( 0 )).size() );
        
        wm.insert( new Cheese( "stilton",
                               7 ) );
        wm.insert( new Cheese( "stilton",
                               8 ) );
        wm.fireAllRules();

        wm = SerializationHelper.getSerialisedStatefulSession( wm );
        results = (List) wm.getGlobal( "results" );
        
        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 1,
                             ((Collection) results.get( 0 )).size() );
        Assert.assertEquals( ArrayList.class.getName(),
                             results.get( 0 ).getClass().getName() );
    }

    public void testExistsWithBinding() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ExistsWithBindings.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Cheese c = new Cheese( "stilton",
                                     10 );
        final Person p = new Person( "Mark",
                                     "stilton" );
        workingMemory.insert( c );
        workingMemory.insert( p );
        workingMemory.fireAllRules();

        assertTrue( list.contains( c.getType() ) );
        assertEquals( 1,
                      list.size() );
    }

    public void testNot() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "not_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        final FactHandle stiltonHandle = workingMemory.insert( stilton );
        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final FactHandle cheddarHandle = workingMemory.insert( cheddar );
        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        workingMemory.retract( stiltonHandle );

        workingMemory.fireAllRules();

        assertEquals( 4,
                      list.size() );
        Assert.assertTrue( list.contains( new Integer( 5 ) ) );
        Assert.assertTrue( list.contains( new Integer( 6 ) ) );
        Assert.assertTrue( list.contains( new Integer( 7 ) ) );
        Assert.assertTrue( list.contains( new Integer( 8 ) ) );
    }

    public void testNotWithBindings() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "not_with_bindings_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        final Rule rule = pkg.getRules()[0];
        assertTrue( rule.isValid() );
        assertEquals( 0,
                      builder.getErrors().getErrors().length );
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        final FactHandle stiltonHandle = workingMemory.insert( stilton );
        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final FactHandle cheddarHandle = workingMemory.insert( cheddar );

        final PersonInterface paul = new Person( "paul",
                                                 "stilton",
                                                 12 );
        workingMemory.insert( paul );
        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        workingMemory.retract( stiltonHandle );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "exists_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final FactHandle cheddarHandle = workingMemory.insert( cheddar );
        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        final FactHandle stiltonHandle = workingMemory.insert( stilton );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );

        final Cheese brie = new Cheese( "brie",
                                        5 );
        final FactHandle brieHandle = workingMemory.insert( brie );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testExists2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_exists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final Cheese provolone = new Cheese( "provolone",
                                             5 );
        final Person edson = new Person( "Edson",
                                         "cheddar" );
        final Person bob = new Person( "Bob",
                                       "muzzarela" );

        workingMemory.insert( cheddar );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.insert( provolone );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.insert( edson );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );

        workingMemory.insert( bob );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );
    }

    public void testForall() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Forall.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final State state = new State( "SP" );
        workingMemory.insert( state );

        final Person bob = new Person( "Bob" );
        bob.setStatus( state.getState() );
        bob.setLikes( "stilton" );
        workingMemory.insert( bob );

        workingMemory.fireAllRules();

        assertEquals( 0,
                      list.size() );

        workingMemory.insert( new Cheese( bob.getLikes(),
                                          10 ) );
        workingMemory.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    public void testRemoveIdentitiesSubNetwork() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_removeIdentitiesSubNetwork.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setRemoveIdentities( true );
        RuleBase ruleBase = getRuleBase( config );
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Person bob = new Person( "bob",
                                       "stilton" );
        workingMemory.insert( bob );

        final Person mark = new Person( "mark",
                                        "stilton" );
        workingMemory.insert( mark );

        final Cheese stilton1 = new Cheese( "stilton",
                                            6 );
        final FactHandle stilton1Handle = workingMemory.insert( stilton1 );
        final Cheese stilton2 = new Cheese( "stilton",
                                            7 );
        final FactHandle stilton2Handle = workingMemory.insert( stilton2 );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        workingMemory.retract( stilton1Handle );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( mark,
                      list.get( 0 ) );

        workingMemory.retract( stilton2Handle );

        workingMemory.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertEquals( bob,
                      list.get( 1 ) );
    }

    public void testCollectWithNestedFromWithParams() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_CollectWithNestedFrom.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        final List results = new ArrayList();
        workingMemory.setGlobal( "results",
                                 results );

        final Person bob = new Person( "bob",
                                       "stilton" );

        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "stilton",
                                        10 ) );
        cheesery.addCheese( new Cheese( "brie",
                                        20 ) );
        cheesery.addCheese( new Cheese( "muzzarela",
                                        8 ) );
        cheesery.addCheese( new Cheese( "stilton",
                                        5 ) );
        cheesery.addCheese( new Cheese( "provolone",
                                        1 ) );

        workingMemory.insert( bob );
        workingMemory.insert( cheesery );

        workingMemory.fireAllRules();

        assertEquals( 1,
                      results.size() );
        List cheeses = (List) results.get( 0 );
        assertEquals( 2,
                      cheeses.size() );
        assertEquals( bob.getLikes(),
                      ((Cheese) cheeses.get( 0 )).getType() );
        assertEquals( bob.getLikes(),
                      ((Cheese) cheeses.get( 1 )).getType() );

    }

    public void testCollectModifyAlphaRestriction() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CollectAlphaRestriction.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        final Cheese[] cheese = new Cheese[]{new Cheese( "stilton",
                                                         10 ), new Cheese( "stilton",
                                                                           2 ), new Cheese( "stilton",
                                                                                            5 ), new Cheese( "brie",
                                                                                                             15 ), new Cheese( "brie",
                                                                                                                               16 ), new Cheese( "provolone",
                                                                                                                                                 8 )};

        final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
        for ( int i = 0; i < cheese.length; i++ ) {
            cheeseHandles[i] = wm.insert( cheese[i] );
        }

        // ---------------- 1st scenario
        int fireCount = 0;
        wm.fireAllRules();
        Assert.assertEquals( ++fireCount,
                             results.size() );
        Assert.assertEquals( 3,
                             ((Collection) results.get( fireCount - 1 )).size() );
        Assert.assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 2nd scenario
        final int index = 1;
        cheese[index].setType( "brie" );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        Assert.assertEquals( ++fireCount,
                             results.size() );
        Assert.assertEquals( 2,
                             ((Collection) results.get( fireCount - 1 )).size() );
        Assert.assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 3rd scenario
        wm.retract( cheeseHandles[2] );
        wm.fireAllRules();

        Assert.assertEquals( ++fireCount,
                             results.size() );
        Assert.assertEquals( 1,
                             ((Collection) results.get( fireCount - 1 )).size() );
        Assert.assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            System.out.println( parser.getErrors() );
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        // load up the rulebase
        return ruleBase;
    }

    public void testForallSinglePattern() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ForallSinglePattern.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        // no cheeses, so should fire
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );

        // only stilton, so should not fire again
        FactHandle stilton1 = workingMemory.insert( new Cheese( "stilton",
                                                                10 ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );

        // only stilton, so should fire again
        FactHandle stilton2 = workingMemory.insert( new Cheese( "stilton",
                                                                11 ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );

        // there is a brie, so should not fire 
        FactHandle brie = workingMemory.insert( new Cheese( "brie",
                                                            10 ) );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );

        // there is a brie, so should not fire 
        workingMemory.retract( stilton1 );
        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );

        // no brie anymore, so should fire 
        workingMemory.retract( brie );
        workingMemory.fireAllRules();
        assertEquals( 2,
                      list.size() );

        //        TODO: in the future, we need to fix the following test case
        //        // no cheese anymore, so should not fire again 
        //        workingMemory.retract( stilton2 );
        //        workingMemory.fireAllRules();
        //        assertEquals( 2,
        //                      list.size() );
        //        

    }

    public void testMVELCollect() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_MVELCollect.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newStatefulSession();
        final List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Cheese( "stilton",
                               10 ) );
        wm.insert( new Cheese( "stilton",
                               7 ) );
        wm.insert( new Cheese( "stilton",
                               8 ) );
        wm.insert( new Cheese( "brie",
                               5 ) );
        wm.insert( new Cheese( "provolone",
                               150 ) );
        wm.insert( new Cheese( "provolone",
                               20 ) );
        wm.insert( new Person( "Bob",
                               "stilton" ) );
        wm.insert( new Person( "Mark",
                               "provolone" ) );

        wm.fireAllRules();

        Assert.assertEquals( 1,
                             results.size() );
        Assert.assertEquals( 6,
                             ((List) results.get( 0 )).size() );
    }

    public void testNestedCorelatedRulesWithForall() throws Exception {

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( FirstOrderLogicTest.class.getResourceAsStream( "test_NestedCorrelatedRulesWithForall.drl" ) ) );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        StatefulSession session = ruleBase.newStatefulSession();

        List list1 = new ArrayList();
        List list2 = new ArrayList();
        List list3 = new ArrayList();
        List list4 = new ArrayList();

        session.setGlobal( "list1",
                           list1 );
        session.setGlobal( "list2",
                           list2 );
        session.setGlobal( "list3",
                           list3 );
        session.setGlobal( "list4",
                           list4 );

        SpecialString first42 = new SpecialString( "42" );
        SpecialString second42 = new SpecialString( "42" );
        SpecialString world = new SpecialString( "World" );

        //System.out.println( "Inserting ..." );

        session.insert( world );
        session.insert( first42 );
        session.insert( second42 );

        //System.out.println( "Done." );

        //System.out.println( "Firing rules ..." );

        // check all lists are empty
        assertTrue( list1.isEmpty() );
        assertTrue( list2.isEmpty() );
        assertTrue( list3.isEmpty() );
        assertTrue( list4.isEmpty() );

        session.fireAllRules();

        //System.out.println( "Done." );

        // check first list is populated correctly
        assertEquals( 0,
                      list1.size() );

        // check second list is populated correctly        
        assertEquals( 0,
                      list2.size() );

        // check third list is populated correctly        
        assertEquals( 1,
                      list3.size() );

        // check fourth list is populated correctly        
        assertEquals( 0,
                      list4.size() );
    }

    public void testFromInsideNotAndExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FromInsideNotAndExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final Cheese provolone = new Cheese( "provolone",
                                             5 );
        final Cheesery cheesery = new Cheesery();

        cheesery.addCheese( cheddar );
        cheesery.addCheese( provolone );

        FactHandle handle = workingMemory.insert( cheesery );
        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        cheesery.addCheese( new Cheese( "stilton",
                                        10 ) );
        cheesery.removeCheese( cheddar );
        workingMemory.update( handle,
                              cheesery );
        workingMemory.fireAllRules();
        assertEquals( 2,
                      list.size() );

    }

    public void testOr() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OrNesting.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Cheese cheddar = new Cheese( "cheddar",
                                           7 );
        final Cheese provolone = new Cheese( "provolone",
                                             5 );
        final Cheese brie = new Cheese( "brie",
                                        15 );
        final Person mark = new Person( "mark",
                                        "stilton" );

        FactHandle ch = workingMemory.insert( cheddar );
        FactHandle ph = workingMemory.insert( provolone );
        FactHandle bh = workingMemory.insert( brie );
        FactHandle markh = workingMemory.insert( mark );

        workingMemory.fireAllRules();
        assertEquals( 1,
                      list.size() );
    }

    public void testCollectWithMemberOfOperators() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_CollectMemberOfOperator.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 1,
                                        "bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        final Order order2 = new Order( 2,
                                        "mark" );
        final OrderItem item21 = new OrderItem( order2,
                                                1 );
        final OrderItem item22 = new OrderItem( order2,
                                                2 );

        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );
        workingMemory.insert( order2 );
        workingMemory.insert( item21 );
        workingMemory.insert( item22 );

        workingMemory.fireAllRules();

        int index = 0;
        assertEquals( 8,
                      list.size() );
        assertSame( order1,
                    list.get( index++ ) );
        assertSame( item11,
                    list.get( index++ ) );
        assertSame( order2,
                    list.get( index++ ) );
        assertSame( item21,
                    list.get( index++ ) );
        assertSame( order1,
                    list.get( index++ ) );
        assertSame( item11,
                    list.get( index++ ) );
        assertSame( order2,
                    list.get( index++ ) );
        assertSame( item21,
                    list.get( index++ ) );

    }

    public void testCollectWithContainsOperators() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_CollectContainsOperator.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        final Order order1 = new Order( 1,
                                        "bob" );
        final OrderItem item11 = new OrderItem( order1,
                                                1 );
        final OrderItem item12 = new OrderItem( order1,
                                                2 );
        final Order order2 = new Order( 2,
                                        "mark" );
        final OrderItem item21 = new OrderItem( order2,
                                                1 );
        final OrderItem item22 = new OrderItem( order2,
                                                2 );

        workingMemory.insert( order1 );
        workingMemory.insert( item11 );
        workingMemory.insert( item12 );
        workingMemory.insert( order2 );
        workingMemory.insert( item21 );
        workingMemory.insert( item22 );

        workingMemory.fireAllRules();

        int index = 0;
        assertEquals( 8,
                      list.size() );
        assertSame( order1,
                    list.get( index++ ) );
        assertSame( item11,
                    list.get( index++ ) );
        assertSame( order2,
                    list.get( index++ ) );
        assertSame( item21,
                    list.get( index++ ) );
        assertSame( order1,
                    list.get( index++ ) );
        assertSame( item11,
                    list.get( index++ ) );
        assertSame( order2,
                    list.get( index++ ) );
        assertSame( item21,
                    list.get( index++ ) );

    }

    public void testForallSinglePatternWithExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ForallSinglePatternWithExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );
        
        workingMemory.insert( new Cheese( "stilton", 10 ) );
        workingMemory.insert( new Cheese( "brie", 10 ) );
        workingMemory.insert( new Cheese( "brie", 10 ) );
        workingMemory.insert( new Order( 1, "bob" ) );
        workingMemory.insert( new Person( "bob", "stilton", 10 ) );
        workingMemory.insert( new Person( "mark", "stilton" ) );
        
        workingMemory.fireAllRules();
        
        //assertEquals( 1, list.size() );
        
    }

    public void testCollectResultBetaConstraint() throws Exception {

        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CollectResultsBetaConstraint.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        StatefulSession wm = ruleBase.newStatefulSession();
        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        wm.insert( new Double( 10 ) );
        wm.insert( new Integer( 2 ) );

//        ruleBase = SerializationHelper.serializeObject( ruleBase );
//        wm = serializeWorkingMemory( ruleBase,
//                                     wm );
//        results = (List) wm.getGlobal( "results" );

        wm.fireAllRules();

        Assert.assertEquals( 0,
                             results.size() );
        
        wm.insert( new Double(15) );
        wm.fireAllRules();
        
        Assert.assertEquals( 2,
                             results.size() );
        
        Assert.assertEquals( "collect",
                             results.get( 0 ) );
        Assert.assertEquals( "accumulate",
                             results.get( 1 ) );
    }

}
