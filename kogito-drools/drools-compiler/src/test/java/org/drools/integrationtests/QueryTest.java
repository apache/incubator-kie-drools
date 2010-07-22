package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.Row;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.impl.FlatQueryResults;
import org.drools.spi.ObjectType;

import junit.framework.TestCase;

public class QueryTest extends TestCase {
    protected RuleBase getRuleBase() throws Exception {

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setMultithreadEvaluation( false );
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    

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
        
        ruleBase.removeQuery( "org.drools.test", "simple query" );
       
        assertNull( ruleBase.getPackage( "org.drools.test" ).getRule( "simple query" ) );
       
        results = session.getQueryResults( "simple query" );
        assertEquals( 0,
                      results.size() );       

    }    
    
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
    
    public void testQuery2KnowledgeBuilder() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_Query.drl", getClass() ), ResourceType.DRL );

        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        kbase.addKnowledgePackages( kbase.getKnowledgePackages() );
        kbase = SerializationHelper.serializeObject( kbase );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();

        final org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "assertedobjquery" );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value1" ),
                      results.iterator().next().get( "assertedobj" ) );
    }    

    public void testQueryWithParams() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_QueryWithParams.drl" ) ) );

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
    
    public void testQueriesWithVariableUnification() throws Exception {
            String str = "";
            str += "package org.drools.test  \n";
            str += "import org.drools.Person \n";
            str += "query peeps( String $name, String $likes, int $age ) \n";
            str += "    $p : Person(name == $name, likes == $likes, age == $age) \n";
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
            Person p1 = new Person( "darth", "stilton", 100 );
            Person p2 = new Person( "yoda", "stilton", 300 );
            Person p3 = new Person( "luke", "brie", 300 );
            Person p4 = new Person( "bobba", "cheddar", 300 );
            

            ksession.insert( p1 );
            ksession.insert( p2 );
            ksession.insert( p3 );
            ksession.insert( p4 );


            //org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "peeps", new Object[] {"darth", "stilton", 100} );
            //org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "peeps", new Object[] { new Variable(), "stilton", 300 } );
            org.drools.runtime.rule.QueryResults results = ksession.getQueryResults( "peeps", new Object[] { new Variable(),new Variable(), 300 } );
            System.out.println( Arrays.asList( results.getIdentifiers() ) );
//            assertEquals( 4,
//                          results.size() );
            for ( org.drools.runtime.rule.QueryResultsRow row : results ) {
                for ( String id : results.getIdentifiers() ) {
                    System.out.print( row.get( id ) + ", " );
                }
                System.out.println();
            }
//            assertEquals( 1,
//                          results.getIdentifiers().length );
            
            
//            Set newSet = new HashSet();
//            for ( org.drools.runtime.rule.QueryResultsRow result : results ) {
//                list = new ArrayList();
//                list.add( result.get( "stilton" ) );
//                list.add( result.get( "cheddar" ) );
//                newSet.add( list );
//            }
//            assertEquals( set,
//                          newSet );
//
//            FlatQueryResults flatResults = new FlatQueryResults( ((StatefulKnowledgeSessionImpl) ksession).session.getQueryResults( "cheeses" ) );
//            assertEquals( 3,
//                          flatResults.size() );
//            assertEquals( 2,
//                          flatResults.getIdentifiers().length );
//            newSet = new HashSet();
//            for ( org.drools.runtime.rule.QueryResultsRow result : flatResults ) {
//                list = new ArrayList();
//                list.add( result.get( "stilton" ) );
//                list.add( result.get( "cheddar" ) );
//                newSet.add( list );
//            }
//            assertEquals( set,
//                          newSet );
        }
    
    public void testOpenQuery() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Cheese \n";
        str += "query cheeses(String $type1, String $type2) \n";
        str += "    stilton : Cheese(type == $type1, $price : price) \n";
        str += "    cheddar : Cheese(type == $type2, price == stilton.price) \n";
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
        
        final List updated = new ArrayList();
        final List removed = new ArrayList();
        final List added = new ArrayList();
        
        ViewChangedEventListener listener = new ViewChangedEventListener() {            
            public void rowUpdated(Row row) {
                updated.add( row.get( "$price" ) );
            }
            
            public void rowRemoved(Row row) {
                removed.add( row.get( "$price" ) );
            }
            
            public void rowAdded(Row row) {
                added.add( row.get( "$price" ) );
            }
        };        
        
        // Open the LiveQuery
        LiveQuery query = ksession.openLiveQuery( "cheeses", new Object[] { "cheddar", "stilton" } , listener );
        
        // Assert that on opening we have three rows added
        assertEquals( 3, added.size() );
        assertEquals( 0, removed.size() );
        assertEquals( 0, updated.size() );
        
        // And that we have correct values from those rows
        assertEquals( 1, added.get( 0 ) );
        assertEquals( 2, added.get( 1 ) );
        assertEquals( 3, added.get( 2 ) );
        
        // Do an update that causes a match to become untrue, thus triggering a removed
        cheddar3.setPrice( 4 );
        ksession.update(  c3Fh, cheddar3 );
        
        assertEquals( 3, added.size() );
        assertEquals( 1, removed.size() );
        assertEquals( 0, updated.size() );
        
        assertEquals( 4, removed.get( 0 ) );
                
        // Now make that partial true again, and thus another added
        cheddar3.setPrice( 3 );
        ksession.update(  c3Fh, cheddar3 );
        
        
        assertEquals( 4, added.size() );
        assertEquals( 1, removed.size() );
        assertEquals( 0, updated.size() );  
        
        assertEquals( 3, added.get( 3 ) );        
        
        // check a standard update
        cheddar3.setOldPrice( 0 );
        ksession.update(  c3Fh, cheddar3 ); 
        
        assertEquals( 4, added.size() );
        assertEquals( 1, removed.size() );
        assertEquals( 1, updated.size() );         
        
        assertEquals( 3, updated.get( 0 ) );      
        
        // Check a standard retract
        ksession.retract( s1Fh );
                
        assertEquals( 4, added.size() );
        assertEquals( 2, removed.size() );
        assertEquals( 1, updated.size() );    
        
        assertEquals( 1, removed.get( 1 ) );          
        
        // Close the query, we should get removed events for each row
        query.close();
        
        assertEquals( 4, added.size() );
        assertEquals( 4, removed.size() );
        assertEquals( 1, updated.size() );         
     
        assertEquals( 2, removed.get( 2 ) );
        assertEquals( 3, removed.get( 3 ) );
        
        // Check that updates no longer have any impact.
        ksession.update(  c3Fh, cheddar3 ); 
        assertEquals( 4, added.size() );
        assertEquals( 4, removed.size() );
        assertEquals( 1, updated.size() );           
    }

}
