package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Address;
import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.InsertedObject;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.Worker;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.compiler.PackageBuilder;
import org.drools.core.util.Entry;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Package;
import org.drools.rule.Variable;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.QueryListenerOption;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.Row;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.impl.FlatQueryResults;
import org.drools.spi.ObjectType;

public class QueryTest {
    protected RuleBase getRuleBase() throws Exception {

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setMultithreadEvaluation( false );
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    @Test
    public void testQuery() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simple_query_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                            ruleBase );
        final QueryResults results = session.getQueryResults( "simple query" );
        assertEquals( 1,
                      results.size() );

    }

    @Test
    public void testQueryRemoval() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "simple_query_test.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );

        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                            ruleBase );
        QueryResults results = session.getQueryResults( "simple query" );
        assertEquals( 1,
                      results.size() );

        assertNotNull( ruleBase.getPackage( "org.drools.test" ).getRule( "simple query" ) );

        ruleBase.removeQuery( "org.drools.test",
                              "simple query" );

        assertNull( ruleBase.getPackage( "org.drools.test" ).getRule( "simple query" ) );

        results = session.getQueryResults( "simple query" );
        assertEquals( 0,
                      results.size() );

    }

    @Test
    public void testQuery2() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Query.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.fireAllRules();

        final QueryResults results = workingMemory.getQueryResults( "assertedobjquery" );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value1" ),
                      results.get( 0 ).get( 0 ) );
    }

    @Test
    public void testQuery2KnowledgeBuilder() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_Query.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        kbase.addKnowledgePackages( kbase.getKnowledgePackages() );
        
        if(kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        kbase = SerializationHelper.serializeObject( kbase );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();

        final org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "assertedobjquery" );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value1" ),
                      results.iterator().next().get( "assertedobj" ) );
    }

    @Test
    public void testQueryWithParams() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_QueryWithParams.drl" ) ) );
        if ( builder.hasErrors()) {
            fail( builder.getErrors().toString() );
        }
        
        
        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.fireAllRules();

        QueryResults results = workingMemory.getQueryResults( "assertedobjquery",
                                                              new String[]{"value1"} );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value1" ),
                      results.get( 0 ).get( 0 ) );

        results = workingMemory.getQueryResults( "assertedobjquery",
                                                 new String[]{"value3"} );
        assertEquals( 0,
                      results.size() );

        results = workingMemory.getQueryResults( "assertedobjquery2",
                                                 new String[]{null, "value2"} );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value2" ),
                      results.get( 0 ).get( 0 ) );

        results = workingMemory.getQueryResults( "assertedobjquery2",
                                                 new String[]{"value3", "value2"} );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value2" ),
                      results.get( 0 ).get( 0 ) );
    }

    @Test
    public void testQueryWithParamsOnKnowledgeApi() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_QueryWithParams.drl",
                                                            getClass() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();

        org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "assertedobjquery",
                                                                                 new String[]{"value1"} );
        assertEquals( 1,
                      results.size() );
        //        assertEquals( new InsertedObject( "value1" ),
        //                      results.get( 0 ).get( 0 ) );

        results = ksession.getQueryResults( "assertedobjquery",
                                            new String[]{"value3"} );
        assertEquals( 0,
                      results.size() );

        results = ksession.getQueryResults( "assertedobjquery2",
                                            new String[]{null, "value2"} );
        assertEquals( 1,
                      results.size() );

        assertEquals( new InsertedObject( "value2" ),
                      ((org.drools.runtime.rule.QueryResultsRow) results.iterator().next()).get( "assertedobj" ) );

        results = ksession.getQueryResults( "assertedobjquery2",
                                            new String[]{"value3", "value2"} );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value2" ),
                      ((org.drools.runtime.rule.QueryResultsRow) results.iterator().next()).get( "assertedobj" ) );
    }

    @Test
    public void testQueryWithMultipleResultsOnKnowledgeApi() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Cheese \n";
        str += "query cheeses \n";
        str += "    stilton : Cheese(type == 'stilton') \n";
        str += "    cheddar : Cheese(type == 'cheddar', price == stilton.price) \n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Cheese stilton1 = new Cheese( "stilton",
                                      1 );
        Cheese cheddar1 = new Cheese( "cheddar",
                                      1 );
        Cheese stilton2 = new Cheese( "stilton",
                                      2 );
        Cheese cheddar2 = new Cheese( "cheddar",
                                      2 );
        Cheese stilton3 = new Cheese( "stilton",
                                      3 );
        Cheese cheddar3 = new Cheese( "cheddar",
                                      3 );

        Set set = new HashSet();
        List list = new ArrayList();
        list.add( stilton1 );
        list.add( cheddar1 );
        set.add( list );

        list = new ArrayList();
        list.add( stilton2 );
        list.add( cheddar2 );
        set.add( list );

        list = new ArrayList();
        list.add( stilton3 );
        list.add( cheddar3 );
        set.add( list );

        ksession.insert( stilton1 );
        ksession.insert( stilton2 );
        ksession.insert( stilton3 );
        ksession.insert( cheddar1 );
        ksession.insert( cheddar2 );
        ksession.insert( cheddar3 );

        org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "cheeses" );
        assertEquals( 3,
                      results.size() );
        assertEquals( 2,
                      results.getIdentifiers().length );
        Set newSet = new HashSet();
        for ( org.drools.runtime.rule.QueryResultsRow result : results ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ) );
            newSet.add( list );
        }
        assertEquals( set,
                      newSet );

        FlatQueryResults flatResults = new FlatQueryResults( ((StatefulKnowledgeSessionImpl) ksession).session.getQueryResults( "cheeses" ) );
        assertEquals( 3,
                      flatResults.size() );
        assertEquals( 2,
                      flatResults.getIdentifiers().length );
        newSet = new HashSet();
        for ( org.drools.runtime.rule.QueryResultsRow result : flatResults ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ) );
            newSet.add( list );
        }
        assertEquals( set,
                      newSet );
    }

    @Test
    public void testTwoQuerries() throws Exception {
        // @see JBRULES-410 More than one Query definition causes an incorrect
        // Rete network to be built.

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_TwoQuerries.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        workingMemory.insert( stilton );
        final Person per1 = new Person( "stinker",
                                        "smelly feet",
                                        70 );
        final Person per2 = new Person( "skunky",
                                        "smelly armpits",
                                        40 );

        workingMemory.insert( per1 );
        workingMemory.insert( per2 );

        QueryResults results = workingMemory.getQueryResults( "find stinky cheeses" );
        assertEquals( 1,
                      results.size() );

        results = workingMemory.getQueryResults( "find pensioners" );
        assertEquals( 1,
                      results.size() );
    }

    @Test
    public void testDoubleQueryWithExists() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_DoubleQueryWithExists.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final Person p1 = new Person( "p1",
                                      "stilton",
                                      20 );
        p1.setStatus( "europe" );
        final FactHandle c1FactHandle = workingMemory.insert( p1 );
        final Person p2 = new Person( "p2",
                                      "stilton",
                                      30 );
        p2.setStatus( "europe" );
        final FactHandle c2FactHandle = workingMemory.insert( p2 );
        final Person p3 = new Person( "p3",
                                      "stilton",
                                      40 );
        p3.setStatus( "europe" );
        final FactHandle c3FactHandle = workingMemory.insert( p3 );
        workingMemory.fireAllRules();

        QueryResults queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p3.setStatus( "america" );
        workingMemory.update( c3FactHandle,
                              p3 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1 ], america=[ 2, 3 ]
        p2.setStatus( "america" );
        workingMemory.update( c2FactHandle,
                              p2 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ ], america=[ 1, 2, 3 ]
        p1.setStatus( "america" );
        workingMemory.update( c1FactHandle,
                              p1 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );

        // europe=[ 2 ], america=[ 1, 3 ]
        p2.setStatus( "europe" );
        workingMemory.update( c2FactHandle,
                              p2 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p1.setStatus( "europe" );
        workingMemory.update( c1FactHandle,
                              p1 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      queryResults.size() );

        // europe=[ 1, 2, 3 ], america=[ ]
        p3.setStatus( "europe" );
        workingMemory.update( c3FactHandle,
                              p3 );
        workingMemory.fireAllRules();
        queryResults = workingMemory.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      queryResults.size() );
    }

    @Test
    public void testQueryWithCollect() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Query.drl" ) ) );

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.fireAllRules();

        final QueryResults results = workingMemory.getQueryResults( "collect objects" );
        assertEquals( 1,
                      results.size() );

        final QueryResult result = results.get( 0 );
        final List list = (List) result.get( "$list" );

        assertEquals( 2,
                      list.size() );
    }

    @Test
    public void testDroolsQueryCleanup() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_QueryMemoryLeak.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        String workerId = "B1234";
        Worker worker = new Worker();
        worker.setId( workerId );

        org.drools.runtime.rule.FactHandle handle = ksession.insert( worker );
        ksession.fireAllRules();

        assertNotNull( handle );

        Object retractedWorker = null;
        for ( int i = 0; i < 100; i++ ) {
            retractedWorker = (Object) ksession.getQueryResults( "getWorker",
                                                                 new Object[]{workerId} );
        }

        assertNotNull( retractedWorker );

        StatefulKnowledgeSessionImpl sessionImpl = (StatefulKnowledgeSessionImpl) ksession;

        ReteooWorkingMemory reteWorkingMemory = sessionImpl.session;
        AbstractWorkingMemory abstractWorkingMemory = (AbstractWorkingMemory) reteWorkingMemory;

        InternalRuleBase ruleBase = (InternalRuleBase) abstractWorkingMemory.getRuleBase();
        Collection<EntryPointNode> entryPointNodes = ruleBase.getRete().getEntryPointNodes().values();

        EntryPointNode defaultEntryPointNode = null;
        for ( EntryPointNode epNode : entryPointNodes ) {
            if ( epNode.getEntryPoint().getEntryPointId() == "DEFAULT" ) {
                defaultEntryPointNode = epNode;
                break;
            }
        }
        assertNotNull( defaultEntryPointNode );

        Map<ObjectType, ObjectTypeNode> obnodes = defaultEntryPointNode.getObjectTypeNodes();

        ObjectType key = new ClassObjectType( DroolsQuery.class );
        ObjectTypeNode droolsQueryNode = obnodes.get( key );
        ObjectHashSet droolsQueryMemory = (ObjectHashSet) abstractWorkingMemory.getNodeMemory( droolsQueryNode );
        assertEquals( 0,
                      droolsQueryMemory.size() );

        Entry[] entries = droolsQueryMemory.getTable();
        int entryCounter = 0;
        for ( Entry entry : entries ) {
            if ( entry != null ) {
                entryCounter++;
                ObjectEntry oEntry = (ObjectEntry) entry;
                DefaultFactHandle factHandle = (DefaultFactHandle) oEntry.getValue();
                assertNull( factHandle.getObject() );
            }
        }
    }

    @Test
    public void testQueriesWithVariableUnification() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Person \n";
        str += "query peeps( String $name, String $likes, int $age ) \n";
        str += "    $p : Person( $name : name, $likes : likes, $age : age ) \n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        Person p3 = new Person( "luke",
                                "brie",
                                300 );
        Person p4 = new Person( "bobba",
                                "cheddar",
                                300 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );

        org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "peeps",
                                                                                 new Object[]{new Variable(), new Variable(), new Variable()} );
        assertEquals( 4,
                          results.size() );
        List names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 4,
                      names.size() );
        assertTrue( names.contains( "luke" ) );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "bobba" ) );
        assertTrue( names.contains( "darth" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{new Variable(), new Variable(), 300} );
        assertEquals( 3,
                          results.size() );
        names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 3,
                      names.size() );
        assertTrue( names.contains( "luke" ) );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "bobba" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{new Variable(), "stilton", 300} );
        assertEquals( 1,
                          results.size() );
        names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 1,
                      names.size() );
        assertTrue( names.contains( "yoda" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{new Variable(), "stilton", new Variable()} );
        assertEquals( 2,
                          results.size() );
        names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 2,
                      names.size() );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "darth" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{"darth", new Variable(), new Variable()} );
        assertEquals( 1,
                          results.size() );
        names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 1,
                      names.size() );
        assertTrue( names.contains( "darth" ) );
    }

    @Test
    public void testQueriesWithVariableUnificationOnPatterns() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Person \n";
        str += "query peeps( Person $p, String $name, String $likes, int $age ) \n";
        str += "    $p : Person( $name : name, $likes : likes, $age : age ) \n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        Person p3 = new Person( "luke",
                                "brie",
                                300 );
        Person p4 = new Person( "bobba",
                                "cheddar",
                                300 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );

        org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "peeps",
                                                                                 new Object[]{new Variable(), new Variable(), new Variable(), new Variable()} );
        assertEquals( 4,
                          results.size() );
        List names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 4,
                      names.size() );
        assertTrue( names.contains( "luke" ) );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "bobba" ) );
        assertTrue( names.contains( "darth" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{p1, new Variable(), new Variable(), new Variable()} );
        assertEquals( 1,
                          results.size() );
        names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 1,
                      names.size() );
        assertTrue( names.contains( "darth" ) );
    }
    
    @Test
    public void testQueriesWithVariableUnificationOnNestedFields() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Person \n";
        str += "query peeps( String $name, String $likes, String $street) \n";
        str += "    $p : Person( $name : name, $likes : likes, $street : address.street ) \n";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        p1.setAddress( new Address("s1") );
        
        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        p2.setAddress( new Address("s2") );

        ksession.insert( p1 );
        ksession.insert( p2 );

        org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "peeps",
                                                                                 new Object[]{new Variable(), new Variable(), new Variable()} );
        assertEquals( 2,
                          results.size() );
        List names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "darth" ) );
        
        

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{ new Variable(), new Variable(), "s1"} );
        assertEquals( 1,
                      results.size() );
        names = new ArrayList();
        for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertTrue( names.contains( "darth" ) );
    }

    @Test
    public void testOpenQuery() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Cheese \n";
        str += "query cheeses(String $type1, String $type2) \n";
        str += "    stilton : Cheese(type == $type1, $sprice : price) \n";
        str += "    cheddar : Cheese(type == $type2, $cprice : price == stilton.price) \n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        Cheese stilton1 = new Cheese( "stilton",
                                      1 );
        Cheese cheddar1 = new Cheese( "cheddar",
                                      1 );
        Cheese stilton2 = new Cheese( "stilton",
                                      2 );
        Cheese cheddar2 = new Cheese( "cheddar",
                                      2 );
        Cheese stilton3 = new Cheese( "stilton",
                                      3 );
        Cheese cheddar3 = new Cheese( "cheddar",
                                      3 );

        org.drools.runtime.rule.FactHandle s1Fh = ksession.insert( stilton1 );
        ksession.insert( stilton2 );
        ksession.insert( stilton3 );
        ksession.insert( cheddar1 );
        ksession.insert( cheddar2 );
        org.drools.runtime.rule.FactHandle c3Fh = ksession.insert( cheddar3 );

        final List<Object[]> updated = new ArrayList<Object[]>();
        final List<Object[]> removed = new ArrayList<Object[]>();
        final List<Object[]> added = new ArrayList<Object[]>();

        ViewChangedEventListener listener = new ViewChangedEventListener() {
            public void rowUpdated(Row row) {
                Object[] array = new Object[6];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                array[2] = row.get( "$sprice" );
                array[3] = row.get( "$cprice" );
                array[4] = row.get( "$type1" );
                array[5] = row.get( "$type2" );
                updated.add( array );
            }

            public void rowRemoved(Row row) {
                Object[] array = new Object[6];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                array[2] = row.get( "$sprice" );
                array[3] = row.get( "$cprice" );
                array[4] = row.get( "$type1" );
                array[5] = row.get( "$type2" );
                removed.add( array );
            }

            public void rowAdded(Row row) {
                Object[] array = new Object[6];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                array[2] = row.get( "$sprice" );
                array[3] = row.get( "$cprice" );
                array[4] = row.get( "$type1" );
                array[5] = row.get( "$type2" );
                added.add( array );
            }
        };

        // Open the LiveQuery
        LiveQuery query = ksession.openLiveQuery( "cheeses",
                                                  new Object[]{"stilton", "cheddar"},
                                                  listener );

        // Assert that on opening we have three rows added
        assertEquals( 3,
                      added.size() );
        assertEquals( 0,
                      removed.size() );
        assertEquals( 0,
                      updated.size() );

        // Assert that the identifiers where retrievable
        assertSame( stilton1,
                    added.get( 0 )[0] );
        assertSame( cheddar1,
                    added.get( 0 )[1] );
        assertEquals( 1,
                      added.get( 0 )[2] );
        assertEquals( 1,
                      added.get( 0 )[3] );
        assertEquals( "stilton",
                      added.get( 0 )[4] );
        assertEquals( "cheddar",
                      added.get( 0 )[5] );

        // And that we have correct values from those rows
        assertEquals( 1,
                      added.get( 0 )[3] );
        assertEquals( 2,
                      added.get( 1 )[3] );
        assertEquals( 3,
                      added.get( 2 )[3] );

        // Do an update that causes a match to become untrue, thus triggering a removed
        cheddar3.setPrice( 4 );
        ksession.update( c3Fh,
                         cheddar3 );

        assertEquals( 3,
                      added.size() );
        assertEquals( 1,
                      removed.size() );
        assertEquals( 0,
                      updated.size() );

        assertEquals( 4,
                      removed.get( 0 )[3] );

        // Now make that partial true again, and thus another added
        cheddar3.setPrice( 3 );
        ksession.update( c3Fh,
                         cheddar3 );

        assertEquals( 4,
                      added.size() );
        assertEquals( 1,
                      removed.size() );
        assertEquals( 0,
                      updated.size() );

        assertEquals( 3,
                      added.get( 3 )[3] );

        // check a standard update
        cheddar3.setOldPrice( 0 );
        ksession.update( c3Fh,
                         cheddar3 );

        assertEquals( 4,
                      added.size() );
        assertEquals( 1,
                      removed.size() );
        assertEquals( 1,
                      updated.size() );

        assertEquals( 3,
                      updated.get( 0 )[3] );

        // Check a standard retract
        ksession.retract( s1Fh );

        assertEquals( 4,
                      added.size() );
        assertEquals( 2,
                      removed.size() );
        assertEquals( 1,
                      updated.size() );

        assertEquals( 1,
                      removed.get( 1 )[3] );

        // Close the query, we should get removed events for each row
        query.close();

        assertEquals( 4,
                      added.size() );
        assertEquals( 4,
                      removed.size() );
        assertEquals( 1,
                      updated.size() );

        assertEquals( 2,
                      removed.get( 2 )[3] );
        assertEquals( 3,
                      removed.get( 3 )[3] );

        // Check that updates no longer have any impact.
        ksession.update( c3Fh,
                         cheddar3 );
        assertEquals( 4,
                      added.size() );
        assertEquals( 4,
                      removed.size() );
        assertEquals( 1,
                      updated.size() );
    }

    @Test
    public void testStandardQueryListener() {
        runQueryListenerTest( QueryListenerOption.STANDARD );
    }

    @Test
    public void testNonCloningQueryListener() {
        runQueryListenerTest( QueryListenerOption.LIGHTWEIGHT );
    }

    public void runQueryListenerTest(QueryListenerOption option) {
        String str = "";
        str += "package org.drools\n";
        str += "query cheeses(String $type) \n";
        str += "    $cheese : Cheese(type == $type) \n";
        str += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( option );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf,
                                                                               null );

        // insert some data into the session
        for ( int i = 0; i < 10000; i++ ) {
            ksession.insert( new Cheese( i % 2 == 0 ? "stilton" : "brie" ) );
        }

        // query the session
        List<Cheese> cheeses;
        for ( int i = 0; i < 100; i++ ) {
            org.drools.runtime.rule.QueryResults queryResults = ksession.getQueryResults( "cheeses",
                                                                                          new Object[]{"stilton"} );
            cheeses = new ArrayList<Cheese>();
            for ( QueryResultsRow row : queryResults ) {
                cheeses.add( (Cheese) row.get( "$cheese" ) );
            }

            assertEquals( 5000,
                          cheeses.size() );
        }

    }

}
