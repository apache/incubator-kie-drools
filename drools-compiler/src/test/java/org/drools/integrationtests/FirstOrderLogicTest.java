package org.drools.integrationtests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Address;
import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.ClockType;
import org.drools.FactA;
import org.drools.FactB;
import org.drools.FactC;
import org.drools.FactHandle;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
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
import org.drools.StockTick;
import org.drools.Triangle;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryConsoleLogger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionClock;
import org.drools.time.SessionPseudoClock;

public class FirstOrderLogicTest {
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase( final RuleBaseConfiguration config ) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    private KnowledgeBase loadKnowledgeBase( String fileName ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( fileName,
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Collection<KnowledgePackage> knowledgePackages = kbuilder.getKnowledgePackages();
        kbase.addKnowledgePackages( knowledgePackages );
        return kbase;
    }

    @Test
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

        assertEquals( 1,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( 0 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( 0 ).getClass().getName() );
    }

    @Test
    public void testCollectNodeSharing() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_collectNodeSharing.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
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
        assertEquals( ++fireCount,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 2nd scenario 
        final int index = 1;
        cheese[index].setPrice( 9 );
        wm.update( cheeseHandles[index],
                   cheese[index] );

        wm.fireAllRules();

        assertEquals( ++fireCount,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 3rd scenario 
        bob.setLikes( "brie" );
        wm.update( bobHandle,
                   bob );
        wm.fireAllRules();

        assertEquals( fireCount,
                             results.size() );

        // ---------------- 4th scenario 
        wm.retract( cheeseHandles[3] );
        wm.fireAllRules();

        // should not have fired as per constraint 
        assertEquals( fireCount,
                             results.size() );
    }

    @Test
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

        assertEquals( 1,
                      results.size() );
        assertEquals( 1,
                      ((Collection) results.get( 0 )).size() );

        wm.insert( new Cheese( "stilton",
                               7 ) );
        wm.insert( new Cheese( "stilton",
                               8 ) );
        wm.fireAllRules();

        wm = SerializationHelper.getSerialisedStatefulSession( wm );
        results = (List) wm.getGlobal( "results" );

        assertEquals( 1,
                      results.size() );
        // It's 3 as while the rule does not fire, it does continue to evaluate and update the collection
        assertEquals( 3,
                      ((Collection) results.get( 0 )).size() );
        assertEquals( ArrayList.class.getName(),
                      results.get( 0 ).getClass().getName() );
    }

    @Test
    public void testExistsWithBinding() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ExistsWithBindings.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testNot() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "not_rule_test.drl" ) ) );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
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
        assertTrue( list.contains( new Integer( 5 ) ) );
        assertTrue( list.contains( new Integer( 6 ) ) );
        assertTrue( list.contains( new Integer( 7 ) ) );
        assertTrue( list.contains( new Integer( 8 ) ) );
    }

    @Test
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
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "exists_rule_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testExists2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_exists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testExists3() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_Exists_JBRULES_2810.drl",
                                                            FirstOrderLogicTest.class ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        WorkingMemoryConsoleLogger logger = new WorkingMemoryConsoleLogger( ksession );
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testForall() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Forall.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testForall2() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_Forall2.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal( "results",
                            list );

        final State state = new State( "SP" );
        ksession.insert( state );

        final Person bob = new Person( "Bob" );
        bob.setStatus( state.getState() );
        bob.setAlive( true );
        ksession.insert( bob );

        ksession.fireAllRules();

        assertEquals( 0,
                      list.size() );

        final State qc = new State( "QC" );
        ksession.insert( qc );
        final Person john = new Person( "John" );
        john.setStatus( qc.getState() );
        john.setAlive( false );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testRemoveIdentitiesSubNetwork() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_removeIdentitiesSubNetwork.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setRemoveIdentities( true );
        RuleBase ruleBase = getRuleBase( config );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testCollectWithNestedFromWithParams() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_CollectWithNestedFrom.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

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

    @Test
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
        assertEquals( ++fireCount,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 2nd scenario 
        final int index = 1;
        cheese[index].setType( "brie" );
        wm.update( cheeseHandles[index],
                   cheese[index] );
        wm.fireAllRules();

        assertEquals( ++fireCount,
                             results.size() );
        assertEquals( 2,
                             ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

        // ---------------- 3rd scenario 
        wm.retract( cheeseHandles[2] );
        wm.fireAllRules();

        assertEquals( ++fireCount,
                             results.size() );
        assertEquals( 1,
                             ((Collection) results.get( fireCount - 1 )).size() );
        assertEquals( ArrayList.class.getName(),
                             results.get( fireCount - 1 ).getClass().getName() );

    }

    private RuleBase loadRuleBase( final Reader reader ) throws IOException,
                                                        DroolsParserException,
                                                        Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            System.out.println( parser.getErrors() );
            fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package 
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase 
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        // load up the rulebase 
        return ruleBase;
    }

    @Test
    public void testForallSinglePattern() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ForallSinglePattern.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );
        int fired = 0;

        // no cheeses, so should fire 
        workingMemory.fireAllRules();
        assertEquals( ++fired,
                      list.size() );

        // only stilton, so should not fire again 
        FactHandle stilton1 = workingMemory.insert( new Cheese( "stilton",
                                                                10 ) );
        workingMemory.fireAllRules();
        assertEquals( fired,
                      list.size() );

        // only stilton, so should not fire again 
        FactHandle stilton2 = workingMemory.insert( new Cheese( "stilton",
                                                                11 ) );
        workingMemory.fireAllRules();
        assertEquals( fired,
                      list.size() );

        // still only stilton, so should not fire  
        workingMemory.retract( stilton1 );
        workingMemory.fireAllRules();
        assertEquals( ++fired, // we need to fix forall to not fire in this situation 
                      list.size() );

        // there is a brie, so should not fire  
        FactHandle brie = workingMemory.insert( new Cheese( "brie",
                                                            10 ) );
        workingMemory.fireAllRules();
        assertEquals( fired,
                      list.size() );

        // no brie anymore, so should fire  
        workingMemory.retract( brie );
        workingMemory.fireAllRules();
        assertEquals( ++fired,
                      list.size() );

        // no more cheese, but since it already fired, should not fire again 
        workingMemory.retract( stilton2 );
        workingMemory.fireAllRules();
        assertEquals( ++fired, // we need to fix forall to not fire in this situation 
                      list.size() );

    }

    @Test
    public void testForallSinglePattern2() throws Exception {
        final KnowledgeBase kbase = loadKnowledgeBase( "test_ForallSinglePattern2.drl" );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Triangle( 3,
                                       3,
                                       3 ) );
        ksession.insert( new Triangle( 3,
                                       3,
                                       3 ) );

        // no cheeses, so should fire 
        int fired = ksession.fireAllRules();
        assertEquals( 1,
                      fired );

        ksession.dispose();
    }

    @Test
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

        assertEquals( 1,
                             results.size() );
        assertEquals( 6,
                             ((List) results.get( 0 )).size() );
    }

    @Test
    public void testNestedCorelatedRulesWithForall() throws Exception {

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( FirstOrderLogicTest.class.getResourceAsStream( "test_NestedCorrelatedRulesWithForall.drl" ) ) );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testFromInsideNotAndExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FromInsideNotAndExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testOr() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OrNesting.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    // JBRULES-2482 
    @Test
    public void testOrWithVariableResolution() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_OrCEFollowedByMultipleEval.drl",
                                                            FirstOrderLogicTest.class ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final AgendaEventListener al = mock( AgendaEventListener.class );
        ksession.addEventListener( al );

        ksession.insert( new FactA( "a" ) );
        ksession.insert( new FactB( "b" ) );
        ksession.insert( new FactC( "c" ) );

        ksession.fireAllRules();
        verify( al,
                times( 6 ) ).afterActivationFired( any( AfterActivationFiredEvent.class ) );
    }

    // JBRULES-2526 
    @Test
    public void testOrWithVariableResolution2() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_OrCEFollowedByMultipleEval2.drl",
                                                            FirstOrderLogicTest.class ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final AgendaEventListener al = mock( AgendaEventListener.class );
        ksession.addEventListener( al );

        ksession.insert( new FactA( "a" ) );
        ksession.insert( new FactB( "b" ) );
        ksession.insert( new FactC( "c" ) );

        ksession.fireAllRules();
        verify( al,
                times( 8 ) ).afterActivationFired( any( AfterActivationFiredEvent.class ) );
    }

    @Test
    public void testCollectWithMemberOfOperators() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_CollectMemberOfOperator.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testCollectWithContainsOperators() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_CollectContainsOperator.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
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

    @Test
    public void testForallSinglePatternWithExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ForallSinglePatternWithExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "results",
                                 list );

        workingMemory.insert( new Cheese( "stilton",
                                          10 ) );
        workingMemory.insert( new Cheese( "brie",
                                          10 ) );
        workingMemory.insert( new Cheese( "brie",
                                          10 ) );
        workingMemory.insert( new Order( 1,
                                         "bob" ) );
        workingMemory.insert( new Person( "bob",
                                          "stilton",
                                          10 ) );
        workingMemory.insert( new Person( "mark",
                                          "stilton" ) );

        workingMemory.fireAllRules();

        //assertEquals( 1, list.size() );

    }

    @Test
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

        assertEquals( 0,
                             results.size() );

        wm.insert( new Double( 15 ) );
        wm.fireAllRules();

        assertEquals( 2,
                             results.size() );

        assertEquals( "collect",
                             results.get( 0 ) );
        assertEquals( "accumulate",
                             results.get( 1 ) );
    }

    @Test
    public void testFromWithOr() throws Exception {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_FromWithOr.drl" ) ),
                     ResourceType.DRL );

        if ( builder.hasErrors() ) {
            System.out.println( builder.getErrors() );
        }
        assertFalse( builder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );

        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        final List<Address> results = new ArrayList<Address>();
        session.setGlobal( "results",
                           results );

        Address a1 = new Address();
        a1.setZipCode( "12345" );
        Address a2 = new Address();
        a2.setZipCode( "54321" );
        Address a3 = new Address();
        a3.setZipCode( "99999" );

        Person p = new Person();
        p.addAddress( a1 );
        p.addAddress( a2 );
        p.addAddress( a3 );

        session.insert( p );
        session.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( a1 ) );
        assertTrue( results.contains( a2 ) );

    }

    @Test
    public void testForallWithSlidingWindow() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_ForallSlidingWindow.drl" ) ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf,
                                                                                     null );
        final SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results",
                            results );

        // advance time... no events, so forall should fire 
        clock.advanceTime( 60,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );

        int seq = 1;
        // advance time... there are matching events now, but forall still not fire 
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 60 
        clock.advanceTime( 5,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 65 
        clock.advanceTime( 5,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );

        // advance time... there are non-matching events now, so forall de-activates 
        ksession.insert( new StockTick( seq++,
                                        "IBM",
                                        10,
                                        clock.getCurrentTime() ) ); // 70 
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );

        // advance time... there are non-matching events now, so forall is still deactivated 
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 80 
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1,
                      results.size() );

        // advance time... non-matching event expires now, so forall should fire 
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 90 
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2,
                      results.size() );

        // advance time... forall still matches and should not fire 
        ksession.insert( new StockTick( seq++,
                                        "RHT",
                                        10,
                                        clock.getCurrentTime() ) ); // 100 
        clock.advanceTime( 10,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, // we need to fix forall to not fire in this situation 
                      results.size() );

        // advance time... forall still matches and should not fire 
        clock.advanceTime( 60,
                           TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 4, // we need to fix forall to not fire in this situation 
                      results.size() );

    }

    @Test
    public void testCollectFromMVELAfterOr() throws Exception {

        // read in the source 
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_CollectFromMVELAfterOr.drl" ) );
        RuleBase ruleBase = loadRuleBase( reader );

        StatefulSession wm = ruleBase.newStatefulSession();
        List results = new ArrayList();

        wm.setGlobal( "results",
                      results );

        Person jill = new Person( "jill" );

        Person bob = new Person( "bob" );
        List addresses = new ArrayList();
        addresses.add( new Address( "a" ) );
        addresses.add( new Address( "b" ) );
        addresses.add( new Address( "c" ) );
        bob.setAddresses( addresses );

        wm.insert( jill );
        wm.insert( bob );

        wm = SerializationHelper.getSerialisedStatefulSession( wm );
        results = (List) wm.getGlobal( "results" );

        wm.fireAllRules();

        assertEquals( 2,
                             results.size() );
        assertEquals( 3,
                             ((Collection) results.get( 0 )).size() );
    }

    @Test
    public void testCollectAfterOrCE() throws Exception {
        //Set up facts
        final Cheesery bonFromage = new Cheesery();
        bonFromage.addCheese( new Cheese( "cheddar" ) );
        bonFromage.addCheese( new Cheese( "cheddar" ) );

        //Test in memory compile of DRL
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_OrCEFollowedByCollect.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        assertFalse( kbuilder.hasErrors() );

        Collection<KnowledgePackage> knowledgePackages = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( knowledgePackages );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.insert( bonFromage );

        int rules = session.fireAllRules();
        assertEquals( 2,
                      rules );

        //Serialize and test again
        knowledgePackages = SerializationHelper.serializeObject( knowledgePackages );
        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( knowledgePackages );
        
        session = kbase.newStatefulKnowledgeSession();
        session.insert( bonFromage );

        rules = session.fireAllRules();
        assertEquals( 2,
                      rules );

    }
    
    @Test @Ignore
    public void testLotsOfOrs() throws Exception {
        // Decomposed this test down to just two rules, while still exhibiting the problem
        // Uncomment rest of rule as those are fixed, to complicate it again.
        String str = "package org.drools.test\n" + 
                "\n" + 
                "import " + FirstOrderLogicTest.class.getCanonicalName() + ".Field;\n" + 
                " \n" + 
                "rule \"test\"\n" + 
                "    when\n" + 
//                "        (\n" + 
//                "            ( \n" + 
//                "                a : Field( name == \"a\") and\n" + 
//                "                eval( !a.getValue().equals(\"a\") ) and\n" + 
//                "                b : Field( name == \"b\" ) and\n" + 
//                "                eval( b.intValue()>10 )\n" + 
//                "           )\n" + 
//                "           /*\n" + 
//                "           or\n" + 
//                "           (\n" + 
//                "                b2 : Field( name == \"b\" ) and\n" + 
//                "                eval( b2.intValue()<10 )\n" + 
//                "           )\n" + 
//                "           */\n" + 
//                "        )\n" + 
//                "        and \n" + 
//                "        (\n" + 
//                "            t : Field( name == \"t\" ) and\n" + 
//                "            eval( t.getValue().equals(\"Y\") )\n" + 
//                "        )\n" + 
//                "        and (\n" + 
//                "           (\n" + 
//                "                c : Field( name == \"c\" ) and\n" + 
//                "                eval( c.getValue().equals(\"c\") ) and\n" +                 
//                "                d : Field( name == \"d\" ) and\n" + 
//                "                eval( d.intValue()<5 )\n" + 
//                "           ) \n" + 
//                "           or \n" + 
                "           (\n" + 
                "                c : Field( name == \"c\" ) and\n" + 
                "                eval( c.getValue().equals(\"c\") ) and\n" + 
                "                d : Field( name == \"d\" ) and\n" + 
                "                eval( d.intValue()<20 )\n" + 
                "           ) \n" + 
                "           or \n" + 
                "           ( \n" + 
                "                c : Field( name == \"c\") and\n" + 
                "                eval( c.getValue().equals(\"d\") ) and\n" + 
                "                d : Field( name == \"d\" ) and\n" + 
                "                eval( d.intValue()<20 )\n" + 
                "           )\n" + 
//                "        )\n" + 
                "    then\n" + 
                "        System.out.println( \"Worked!\" ); \n" + 
                "end";
        
        System.out.println( str );
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
             
        ksession.insert(new Field("t", "Y"));
        ksession.insert(new Field("a", "b"));
        ksession.insert(new Field("b", "15"));
        ksession.insert(new Field("c", "d"));
        ksession.insert(new Field("d", "15"));
        ksession.fireAllRules();   
        ksession.dispose();
    }
    

    public class Field {
        public Field(String name, String value) {
            super();
            this.name = name;
            this.value = value;
        }
        
        private String name;
        private String value;
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        
        public int intValue() {
            Integer intValue = Integer.valueOf(value);
            return intValue;
        }
    }    
}
