/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.compiler.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;

import org.drools.compiler.Address;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.DomainObject;
import org.drools.compiler.InsertedObject;
import org.drools.compiler.Interval;
import org.drools.compiler.Person;
import org.drools.compiler.Worker;
import org.drools.core.QueryResultsImpl;
import org.drools.core.QueryResultsRowImpl;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.runtime.rule.impl.FlatQueryResultRow;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.drools.core.spi.ObjectType;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.rule.Rule;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.Variable;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.utils.KieHelper;

public class QueryTest extends CommonTestMethodBase {

    @org.junit.Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
       System.out.println( "] " + testName.getMethodName());
    }

    private static QueryResults getQueryResults(KieSession session, String queryName, Object... arguments ) throws Exception {
        QueryResultsImpl results = (QueryResultsImpl) session.getQueryResults( queryName, arguments );

        FlatQueryResults flatResults = new FlatQueryResults(results);

        assertEquals( "Query results size", results.size(), flatResults.size() );
        assertEquals( "Query results identifiers", results.getIdentifiers().length, flatResults.getIdentifiers().length );
        Set<String> resultIds = new TreeSet<String>(Arrays.asList(results.getIdentifiers()));
        Set<String> flatIds = new TreeSet<String>(Arrays.asList(flatResults.getIdentifiers()));
        assertArrayEquals("Flat query results identifiers", resultIds.toArray(), flatIds.toArray() );

        FlatQueryResults copyFlatResults = roundTrip(flatResults);
        String [] identifiers = results.getIdentifiers();
        Iterator<QueryResultsRow> copyFlatIter = copyFlatResults.iterator();
        for( int i = 0; i < results.size(); ++i ) {
            QueryResultsRow row = results.get(i);
            assertTrue( "Round-tripped flat query results contain less rows than original query results", copyFlatIter.hasNext());
            QueryResultsRow copyRow = copyFlatIter.next();
            for( String id : identifiers ) {
                Object obj = row.get(id);
                if( obj != null ) {
                    Object copyObj = copyRow.get(id);
                    assertTrue( "Flat query result [" + i + "] does not contain result: '" + id + "': " + obj + "/" + copyObj, obj != null && obj.equals(copyObj));
                }
                FactHandle fh = row.getFactHandle(id);
                FactHandle copyFh = copyRow.getFactHandle(id);
                if( fh != null ) {
                    assertNotNull( "Flat query result [" + i + "] does not contain facthandle: '" + ((InternalFactHandle) fh).getId() + "'", copyFh);
                    String fhStr = fh.toExternalForm();
                    fhStr = fhStr.substring(0, fhStr.lastIndexOf(":"));
                    String copyFhStr = copyFh.toExternalForm();
                    copyFhStr = copyFhStr.substring(0, copyFhStr.lastIndexOf(":"));
                    assertEquals( "Unequal fact handles for fact handle '" + ((InternalFactHandle) fh).getId() + "':",
                                  fhStr, copyFhStr );
                }
            }
        }

        // check identifiers
        Set<String> copyFlatIds = new TreeSet<String>(Arrays.asList(copyFlatResults.getIdentifiers()));
        assertArrayEquals("Flat query results identifiers", flatIds.toArray(), copyFlatIds.toArray() );
        return copyFlatResults;
    }


    private static <T> T roundTrip( Object obj ) throws Exception {
        Class[] classes = { obj.getClass() };
        JAXBContext ctx = getJaxbContext(classes);
        String xmlOut = marshall(ctx, obj);
        return unmarshall(ctx, xmlOut);
    }

    private static <T> T unmarshall( JAXBContext ctx, String xmlIn ) throws Exception {
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlIn.getBytes(Charset.forName("UTF-8")));
        Object out = ctx.createUnmarshaller().unmarshal(xmlStrInputStream);
        return (T) out;
    }

    private static String marshall( JAXBContext ctx, Object obj ) throws Exception {
        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(obj, writer);
        return writer.getBuffer().toString();
    }

    private static JAXBContext getJaxbContext( Class<?>... classes ) throws Exception {
        List<Class<?>> jaxbClassList = new ArrayList<Class<?>>();
        jaxbClassList.addAll(Arrays.asList(classes));
        jaxbClassList.add(Cheese.class);
        jaxbClassList.add(InsertedObject.class);
        jaxbClassList.add(Person.class);
        Class<?>[] jaxbClasses = jaxbClassList.toArray(new Class[jaxbClassList.size()]);
        return JAXBContext.newInstance(jaxbClasses);
    }

    @Test
    public void testQuery() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("simple_query_test.drl"));
        KieSession session = createKieSession( kbase );

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        FactHandle factHandle = session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        String queryName = "simple query";
        org.kie.api.runtime.rule.QueryResults results = getQueryResults(session, queryName);
        assertEquals( 1,
                      results.size() );

        QueryResultsRow row = results.iterator().next();
        if( row instanceof FlatQueryResultRow ) {
            FlatQueryResultRow flatRow = (FlatQueryResultRow) row;
            assertEquals( 0, flatRow.getIdentifiers().size() );
        } else if( row instanceof QueryResultsRowImpl ) {
            QueryResultsRowImpl rowImpl = (QueryResultsRowImpl) row;
            assertEquals( 0, rowImpl.getDeclarations().size() );
        }
    }

    @Test
    public void testQueryRemoval() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("simple_query_test.drl"));
        KieSession session = createKieSession( kbase );

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        org.kie.api.runtime.rule.QueryResults results = session.getQueryResults( "simple query" );
        assertEquals( 1,
                      results.size() );

        Rule rule = kbase.getKiePackage( "org.drools.compiler.test" ).getRules().iterator().next();

        assertEquals( "simple query",
                      rule.getName());

        kbase.removeQuery( "org.drools.compiler.test",
                           "simple query" );

        assertTrue( kbase.getKiePackage( "org.drools.compiler.test" ).getRules().isEmpty() );

        try {
            results = session.getQueryResults( "simple query" );
        } catch ( Exception e ) {
            assertTrue( e.getMessage().endsWith( "does not exist") );
        }
    }

    @Test
    public void testQuery2() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_Query.drl"));
        KieSession session = createKieSession( kbase );

        session.fireAllRules();

        QueryResults results = getQueryResults(session, "assertedobjquery" );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value1" ),
                      ((InternalFactHandle) results.iterator().next().getFactHandle( "assertedobj" )).getObject() );
    }

    @Test
    public void testQueryWithParams() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_QueryWithParams.drl"));
        KieSession session = createKieSession( kbase );

        session.fireAllRules();

        String queryName = "assertedobjquery";
        String [] arguments = new String[]{"value1"};
        QueryResultsImpl resultsImpl = (QueryResultsImpl) session.getQueryResults( queryName, arguments );

        QueryResults results = getQueryResults( session, queryName, arguments );

        assertEquals( 1,
                      results.size() );
        InsertedObject value = new InsertedObject( "value1" );
        assertEquals( value,
                      ((InternalFactHandle) results.iterator().next().getFactHandle( "assertedobj" )).getObject()  );
        assertEquals( value,
                      results.iterator().next().get("assertedobj") );

        results = getQueryResults( session, "assertedobjquery", new String[]{"value3"}  );

        assertEquals( 0,
                      results.size() );

        results = getQueryResults( session, "assertedobjquery2", new String[]{null, "value2"}  );
        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value2" ),
                      ((InternalFactHandle) results.iterator().next().getFactHandle( "assertedobj" )).getObject() );

        results = getQueryResults(session, "assertedobjquery2", new String[]{"value3", "value2"}  );

        assertEquals( 1,
                      results.size() );
        assertEquals( new InsertedObject( "value2" ),
                      ((InternalFactHandle) results.iterator().next().getFactHandle( "assertedobj" )).getObject()  );
    }

    @Test
    public void testQueryWithMultipleResultsOnKnowledgeApi() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.compiler.Cheese \n";
        str += "query cheeses \n";
        str += "    stilton : Cheese(type == 'stilton') \n";
        str += "    cheddar : Cheese(type == 'cheddar', price == stilton.price) \n";
        str += "end\n";

        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        KieSession session = createKieSession( kbase );


        Cheese stilton1 = new Cheese( "stilton", 1 );
        Cheese cheddar1 = new Cheese( "cheddar", 1 );
        Cheese stilton2 = new Cheese( "stilton", 2 );
        Cheese cheddar2 = new Cheese( "cheddar", 2 );
        Cheese stilton3 = new Cheese( "stilton", 3 );
        Cheese cheddar3 = new Cheese( "cheddar", 3 );

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

        session.insert( stilton1 );
        session.insert( stilton2 );
        session.insert( stilton3 );
        session.insert( cheddar1 );
        session.insert( cheddar2 );
        session.insert( cheddar3 );

        org.kie.api.runtime.rule.QueryResults results = getQueryResults(session, "cheeses" );
        assertEquals( 3, results.size() );
        assertEquals( 2, results.getIdentifiers().length );

        Set newSet = new HashSet();
        for ( org.kie.api.runtime.rule.QueryResultsRow result : results ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ) );
            newSet.add( list );
        }
        assertEquals( set,
                      newSet );

        FlatQueryResults flatResults = new FlatQueryResults( ((StatefulKnowledgeSessionImpl) session).getQueryResults( "cheeses" ) );

        newSet = new HashSet();
        for ( org.kie.api.runtime.rule.QueryResultsRow result : flatResults ) {
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
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_TwoQuerries.drl"));
        KieSession session = createKieSession( kbase );

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        session.insert( stilton );
        final Person per1 = new Person( "stinker",
                                        "smelly feet",
                                        70 );
        final Person per2 = new Person( "skunky",
                                        "smelly armpits",
                                        40 );

        session.insert( per1 );
        session.insert( per2 );

        QueryResults results = getQueryResults( session, "find stinky cheeses" );
        assertEquals( 1,
                      results.size() );

        results = getQueryResults( session, "find pensioners" );
        assertEquals( 1,
                      results.size() );
    }

    @Test
    public void testDoubleQueryWithExists() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_DoubleQueryWithExists.drl"));
        KieSession session = createKieSession( kbase );

        final Person p1 = new Person( "p1",
                                      "stilton",
                                      20 );
        p1.setStatus( "europe" );
        final FactHandle c1FactHandle = session.insert( p1 );
        final Person p2 = new Person( "p2",
                                      "stilton",
                                      30 );
        p2.setStatus( "europe" );
        final FactHandle c2FactHandle = session.insert( p2 );
        final Person p3 = new Person( "p3",
                                      "stilton",
                                      40 );
        p3.setStatus( "europe" );
        final FactHandle c3FactHandle = session.insert( p3 );
        session.fireAllRules();

        QueryResults results = session.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      results.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p3.setStatus( "america" );
        session.update( c3FactHandle,
                              p3 );
        session.fireAllRules();
        results = session.getQueryResults(  "2 persons with the same status" );
        assertEquals( 1,
                      results.size() );

        // europe=[ 1 ], america=[ 2, 3 ]
        p2.setStatus( "america" );
        session.update( c2FactHandle,
                              p2 );
        session.fireAllRules();
        results = session.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      results.size() );

        // europe=[ ], america=[ 1, 2, 3 ]
        p1.setStatus( "america" );
        session.update( c1FactHandle,
                              p1 );
        session.fireAllRules();
        results = getQueryResults( session, "2 persons with the same status" );
        assertEquals( 2,
                      results.size() );

        // europe=[ 2 ], america=[ 1, 3 ]
        p2.setStatus( "europe" );
        session.update( c2FactHandle,
                              p2 );
        session.fireAllRules();
        results = getQueryResults( session, "2 persons with the same status" );
        assertEquals( 1,
                      results.size() );

        // europe=[ 1, 2 ], america=[ 3 ]
        p1.setStatus( "europe" );
        session.update( c1FactHandle,
                              p1 );
        session.fireAllRules();
        results = session.getQueryResults( "2 persons with the same status" );
        assertEquals( 1,
                      results.size() );

        // europe=[ 1, 2, 3 ], america=[ ]
        p3.setStatus( "europe" );
        session.update( c3FactHandle,
                              p3 );
        session.fireAllRules();
        results = session.getQueryResults( "2 persons with the same status" );
        assertEquals( 2,
                      results.size() );
    }

    @Test
    public void testQueryWithCollect() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_Query.drl"));
        KieSession session = createKieSession( kbase );
        session.fireAllRules();

        QueryResults results = getQueryResults( session, "collect objects" );
        assertEquals( 1,
                      results.size() );

        final QueryResultsRow row = results.iterator().next();
        final List list = (List) row.get( "$list" );

        assertEquals( 2,
                      list.size() );
    }

    @Test
    public void testDroolsQueryCleanup() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_QueryMemoryLeak.drl"));
        KieSession session = createKieSession( kbase );

        KieSession ksession = kbase.newKieSession();

        String workerId = "B1234";
        Worker worker = new Worker();
        worker.setId( workerId );

        FactHandle handle = ksession.insert( worker );
        ksession.fireAllRules();

        assertNotNull( handle );

        Object retractedWorker = null;
        for ( int i = 0; i < 100; i++ ) {
            retractedWorker = (Object) ksession.getQueryResults( "getWorker",
                                                                 new Object[]{workerId} );
        }

        assertNotNull( retractedWorker );

        StatefulKnowledgeSessionImpl sessionImpl = (StatefulKnowledgeSessionImpl) ksession;

        Collection<EntryPointNode> entryPointNodes = sessionImpl.getKnowledgeBase().getRete().getEntryPointNodes().values();

        EntryPointNode defaultEntryPointNode = null;
        for ( EntryPointNode epNode : entryPointNodes ) {
            if ( epNode.getEntryPoint().getEntryPointId().equals( "DEFAULT" ) ) {
                defaultEntryPointNode = epNode;
                break;
            }
        }
        assertNotNull( defaultEntryPointNode );

        Map<ObjectType, ObjectTypeNode> obnodes = defaultEntryPointNode.getObjectTypeNodes();

        ObjectType key = new ClassObjectType( DroolsQuery.class );
        ObjectTypeNode droolsQueryNode = obnodes.get( key );
        Iterator<InternalFactHandle> it = ((ObjectTypeNodeMemory) sessionImpl.getNodeMemory( droolsQueryNode )).iterator();
        assertFalse( it.hasNext() );
    }

    @Test
    public void testQueriesWithVariableUnification() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.compiler.Person \n";
        str += "query peeps( String $name, String $likes, int $age ) \n";
        str += "    $p : Person( $name := name, $likes := likes, $age := age ) \n";
        str += "end\n";

        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        KieSession ksession = createKieSession( kbase );

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

        QueryResults results = getQueryResults(ksession, "peeps", new Object[]{Variable.v, Variable.v, Variable.v} );
        assertEquals( 4, results.size() );
        List names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 4,
                      names.size() );
        assertTrue( names.contains( "luke" ) );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "bobba" ) );
        assertTrue( names.contains( "darth" ) );

        results = getQueryResults(ksession, "peeps", new Object[]{Variable.v, Variable.v, 300} );
        assertEquals( 3, results.size() );
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 3,
                      names.size() );
        assertTrue( names.contains( "luke" ) );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "bobba" ) );

        results = getQueryResults(ksession, "peeps", new Object[]{Variable.v, "stilton", 300} );
        assertEquals( 1, results.size() );
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 1,
                      names.size() );
        assertTrue( names.contains( "yoda" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{Variable.v, "stilton", Variable.v} );
        assertEquals( 2,
                          results.size() );
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 2,
                      names.size() );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "darth" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{"darth", Variable.v, Variable.v} );
        assertEquals( 1,
                          results.size() );
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 1,
                      names.size() );
        assertTrue( names.contains( "darth" ) );
    }

    @Test
    public void testQueriesWithVariableUnificationOnPatterns() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.compiler.Person \n";
        str += "query peeps( Person $p, String $name, String $likes, int $age ) \n";
        str += "    $p := Person( $name := name, $likes := likes, $age := age ) \n";
        str += "end\n";

        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        KieSession ksession = createKieSession( kbase );

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

        org.kie.api.runtime.rule.QueryResults results = ksession.getQueryResults( "peeps",
                                                                                 new Object[]{Variable.v, Variable.v, Variable.v, Variable.v} );
        assertEquals( 4,
                          results.size() );
        List names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 4,
                      names.size() );
        assertTrue( names.contains( "luke" ) );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "bobba" ) );
        assertTrue( names.contains( "darth" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{p1, Variable.v, Variable.v, Variable.v} );
        assertEquals( 1,
                          results.size() );
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 1,
                      names.size() );
        assertTrue( names.contains( "darth" ) );
    }

    @Test
    public void testQueriesWithVariableUnificationOnNestedFields() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.compiler.Person \n";
        str += "query peeps( String $name, String $likes, String $street) \n";
        str += "    $p : Person( $name := name, $likes := likes, $street := address.street ) \n";
        str += "end\n";

        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        KieSession ksession = createKieSession( kbase );

        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        p1.setAddress( new Address( "s1" ) );

        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        p2.setAddress( new Address( "s2" ) );

        ksession.insert( p1 );
        ksession.insert( p2 );

        QueryResults results = getQueryResults( ksession, "peeps", new Object[]{Variable.v, Variable.v, Variable.v} );
        assertEquals( 2,
                      results.size() );
        List names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "darth" ) );

        results = getQueryResults( ksession, "peeps", new Object[]{Variable.v, Variable.v, "s1"} );
        assertEquals( 1,
                      results.size() );
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertTrue( names.contains( "darth" ) );
    }

    @Test
    public void testOpenQuery() throws Exception {
        if( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
            return;  //Disbaled due to phreak, as tests is order specific
        }
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.compiler.Cheese \n";
        str += "query cheeses(String $type1, String $type2) \n";
        str += "    stilton : Cheese(type == $type1, $sprice : price) \n";
        str += "    cheddar : Cheese(type == $type2, $cprice : price == stilton.price) \n";
        str += "end\n";

        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        KieSession ksession = createKieSession( kbase );

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

        FactHandle s1Fh = ksession.insert( stilton1 );
        ksession.insert( stilton2 );
        ksession.insert( stilton3 );
        ksession.insert( cheddar1 );
        ksession.insert( cheddar2 );
        FactHandle c3Fh = ksession.insert( cheddar3 );

        final List<Object[]> updated = new ArrayList<Object[]>();
        final List<Object[]> removed = new ArrayList<Object[]>();
        final List<Object[]> added = new ArrayList<Object[]>();

        ViewChangedEventListener listener = new ViewChangedEventListener() {
            public void rowUpdated( Row row ) {
                Object[] array = new Object[6];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                array[2] = row.get( "$sprice" );
                array[3] = row.get( "$cprice" );
                array[4] = row.get( "$type1" );
                array[5] = row.get( "$type2" );
                updated.add( array );
            }

            public void rowDeleted( Row row ) {
                Object[] array = new Object[6];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                array[2] = row.get( "$sprice" );
                array[3] = row.get( "$cprice" );
                array[4] = row.get( "$type1" );
                array[5] = row.get( "$type2" );
                removed.add( array );
            }

            public void rowInserted( Row row ) {
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

        ksession.fireAllRules();

        // Assert that on opening we have three rows added
        assertEquals( 3,
                      added.size() );
        assertEquals( 0,
                      removed.size() );
        assertEquals( 0,
                      updated.size() );

        // Assert that the identifiers where retrievable
        assertSame( stilton1,
                    added.get( 2 )[0] );
        assertSame( cheddar1,
                    added.get( 2 )[1] );
        assertEquals( 1,
                      added.get( 2 )[2] );
        assertEquals( 1,
                      added.get( 2 )[3] );
        assertEquals( "stilton",
                      added.get( 2 )[4] );
        assertEquals( "cheddar",
                      added.get( 2 )[5] );

        // And that we have correct values from those rows
        assertEquals( 3,
                      added.get( 0 )[3] );
        assertEquals( 2,
                      added.get( 1 )[3] );
        assertEquals( 1,
                      added.get( 2 )[3] );

        // Do an update that causes a match to become untrue, thus triggering a removed
        cheddar3.setPrice( 4 );
        ksession.update( c3Fh,
                         cheddar3 );
        ksession.fireAllRules();

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
        ksession.fireAllRules();

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
        ksession.fireAllRules();

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
        ksession.fireAllRules();

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

        ksession.fireAllRules();

        assertEquals( 4,
                      added.size() );
        assertEquals( 4,
                      removed.size() );
        assertEquals( 1,
                      updated.size() );

        assertEquals( 2,
                      removed.get( 3 )[3] );
        assertEquals( 3,
                      removed.get( 2 )[3] );

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
    public void testStandardQueryListener() throws IOException, ClassNotFoundException {
        runQueryListenerTest( QueryListenerOption.STANDARD );
    }

    @Test
    public void testNonCloningQueryListener() throws IOException, ClassNotFoundException {
        runQueryListenerTest( QueryListenerOption.LIGHTWEIGHT );
    }

    public void runQueryListenerTest( QueryListenerOption option ) throws IOException, ClassNotFoundException {
        String str = "";
        str += "package org.drools.compiler.integrationtests\n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "query cheeses(String $type) \n";
        str += "    $cheese : Cheese(type == $type) \n";
        str += "end\n";

        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        KieSession ksession = createKieSession( kbase, option );

        // insert some data into the session
        for ( int i = 0; i < 10000; i++ ) {
            ksession.insert( new Cheese( i % 2 == 0 ? "stilton" : "brie" ) );
        }

        // query the session
        List<Cheese> cheeses;
        for ( int i = 0; i < 100; i++ ) {
            org.kie.api.runtime.rule.QueryResults queryResults = ksession.getQueryResults( "cheeses",
                                                                                          new Object[]{"stilton"} );
            cheeses = new ArrayList<Cheese>();
            for ( QueryResultsRow row : queryResults ) {
                cheeses.add( (Cheese) row.get( "$cheese" ) );
            }

            assertEquals( 5000,
                          cheeses.size() );
        }
    }

    @Test
    public void testQueryWithEval() throws IOException, ClassNotFoundException {
        // [Regression in 5.2.0.M2]: NPE during rule evaluation on MVELPredicateExpression.evaluate(MVELPredicateExpression.java:82)

        String str = "package org.drools.compiler.integrationtests\n" +
                     "import " + DomainObject.class.getCanonicalName() + " \n" +
                     "query queryWithEval \n" +
                     "    $do: DomainObject()\n" +
                     "    not DomainObject( id == $do.id, eval(interval.isAfter($do.getInterval())))\n" +
                     "end";

        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        KieSession ksession = createKieSession( kbase );

        DomainObject do1 = new DomainObject();
        do1.setId( 1 );
        do1.setInterval( new Interval( 10,
                                       5 ) );
        DomainObject do2 = new DomainObject();
        do2.setId( 1 );
        do2.setInterval( new Interval( 20,
                                       5 ) );
        ksession.insert( do1 );
        ksession.insert( do2 );

        org.kie.api.runtime.rule.QueryResults results = ksession.getQueryResults( "queryWithEval" );
        assertEquals( 1,
                      results.size() );
        assertEquals( do2,
                      results.iterator().next().get( "$do" ) );

        ksession.dispose();
    }

    @Test
    public void testQueryWithIncompatibleArgs() {
        String drl = "global java.util.List list; " +
                     "" +
                     "query foo( String $s, String $s, String $s ) end " +
                     "" +
                     "rule React \n" +
                     "when\n" +
                     "  $i : Integer() " +
                     "  foo( $i, $x, $i ; ) " +
                     "then\n" +
                     "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        Results results = helper.verify();
        assertTrue( results.hasMessages( Message.Level.ERROR ) );
        assertEquals( 2, results.getMessages( Message.Level.ERROR ).size() );
    }

    @Test
    public void testQueryWithSyntaxError() {
        String drl = "global java.util.List list; " +
                     "" +
                     "query foo( Integer $i ) end " +
                     "" +
                     "rule React \n" +
                     "when\n" +
                     "  $i : Integer() " +
                     "  foo( $i ) " +   // missing ";" should result in 1 compilation error
                     "then\n" +
                     "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        Results results = helper.verify();
        assertTrue( results.hasMessages( Message.Level.ERROR ) );
        assertEquals( 1, results.getMessages( Message.Level.ERROR ).size() );
    }

    @Test
    public void testQueryWithWrongParamNumber() {
        String drl = "global java.util.List list; " +
                     "" +
                     "query foo( Integer $i ) end " +
                     "" +
                     "rule React \n" +
                     "when\n" +
                     "  $i : Integer() " +
                     "  $j : Integer() " +
                     "  foo( $i, $j ; ) " +
                    "then\n" +
                     "end";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        Results results = helper.verify();
        assertTrue( results.hasMessages( Message.Level.ERROR ) );
        assertEquals( 1, results.getMessages( Message.Level.ERROR ).size() );
    }



    @Test
    public void testGlobalsInQueries() {
        String drl = "\n" +
                     "package com.sample\n" +
                     "\n" +
                     "global java.lang.String AString;\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare AThing\n" +
                     "     name: String @key\n" +
                     "end\n" +
                     "\n" +
                     "rule init\n" +
                     "     when\n" +
                     "     then\n" +
                     "         insert( new AThing( AString ) );\n" +
                     "         insert( new AThing( 'Holla' ) );\n" +
                     "end\n" +
                     "\n" +
                     "query test( String $in ) \n" +
                     "     AThing( $in; )\n" +
                     "end\n" +
                     "\n" +
                     "rule spot\n" +
                     "     when\n" +
                     "         test( \"Hello\"; )\n" +
                     "         AThing( \"Hello\"; )\n" +
                     "         test( AString; )\n" +
                     "         AThing( AString; )" +
                     "     then\n" +
                     "         list.add( AString + \" World\" );\n" +
                     "end\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ks = helper.build(  ).newKieSession();

        ArrayList list = new ArrayList();
        ks.setGlobal( "AString", "Hello" );
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( Arrays.asList( "Hello World" ), list );
    }


    @Test
    public void testQueryWithClassArg() {
        //DROOLS-590
        String drl = "global java.util.List list; " +
                     "" +
                     "declare Foo end " +
                     "" +
                     "query bar( Class $c ) " +
                     "  Class( this.getName() == $c.getName() ) " +
                     "end " +
                     "query bar2( Class $c ) " +
                     "  Class( this == $c ) " +
                     "end " +
                     "" +
                     "rule Init when then insert( Foo.class ); end " +
                     "" +
                     "rule React1 " +
                     "when " +
                     "  bar( Foo.class ; ) " +
                     "then " +
                     "  list.add( 'aa' ); " +
                     "end  " +

                     "rule React2 " +
                     "when\n" +
                     "  bar2( Foo.class ; ) " +
                     "then " +
                     "  list.add( 'bb' ); " +
                     "end";

        List list = new ArrayList(  );
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ks = helper.build(  ).newKieSession();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( Arrays.asList( "aa", "bb" ), list );
    }

    @Test
    public void testPassGlobalToNestedQuery() {
        // DROOLS-851
        String drl = "global java.util.List list;\n" +
                     "global Integer number;\n" +
                     "\n" +
                     "query findString( String $out )\n" +
                     "    findStringWithLength( number, $out; )\n" +
                     "end\n" +
                     "query findStringWithLength( int $in, String $out )\n" +
                     "    $out := String( $in := length )\n" +
                     "end\n" +
                     "\n" +
                     "rule R when\n" +
                     "    findString( $s; )\n" +
                     "then\n" +
                     "    list.add( $s );\n" +
                     "end\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession ks = helper.build(  ).newKieSession();

        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.setGlobal( "number", 3 );

        ks.insert( "Hi" );
        ks.insert( "Bye" );
        ks.insert( "Hello" );
        ks.fireAllRules();

        assertEquals( Arrays.asList( "Bye" ), list );
    }
}
