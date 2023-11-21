/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.DroolsQuery;
import org.drools.base.base.ObjectType;
import org.drools.commands.runtime.FlatQueryResults;
import org.drools.core.QueryResultsImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReteDumper;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.DomainObject;
import org.drools.mvel.compiler.InsertedObject;
import org.drools.mvel.compiler.Interval;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.Worker;
import org.drools.mvel.compiler.oopath.model.Thing;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.Variable;
import org.kie.api.runtime.rule.ViewChangedEventListener;

import jakarta.xml.bind.JAXBContext;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class QueryTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public QueryTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @org.junit.Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
       System.out.println( "] " + testName.getMethodName());
    }

    private static QueryResults getQueryResults(KieSession session, String queryName, Object... arguments ) throws Exception {
        QueryResultsImpl results = (QueryResultsImpl) session.getQueryResults( queryName, arguments );

        FlatQueryResults flatResults = new FlatQueryResults(results);

        assertThat(flatResults.size()).as("Query results size").isEqualTo(results.size());
        assertThat(flatResults.getIdentifiers().length).as("Query results identifiers").isEqualTo(results.getIdentifiers().length);
        Set<String> resultIds = new TreeSet<String>(Arrays.asList(results.getIdentifiers()));
        Set<String> flatIds = new TreeSet<String>(Arrays.asList(flatResults.getIdentifiers()));
        assertThat(resultIds.toArray()).as("Flat query results identifiers").isEqualTo(flatIds.toArray());

        FlatQueryResults copyFlatResults = roundTrip(flatResults);
        String [] identifiers = results.getIdentifiers();
        Iterator<QueryResultsRow> copyFlatIter = copyFlatResults.iterator();
        for( int i = 0; i < results.size(); ++i ) {
            QueryResultsRow row = results.get(i);
            assertThat(copyFlatIter.hasNext()).as("Round-tripped flat query results contain less rows than original query results").isTrue();
            QueryResultsRow copyRow = copyFlatIter.next();
            for( String id : identifiers ) {
                Object obj = row.get(id);
                if( obj != null ) {
                    Object copyObj = copyRow.get(id);
                    assertThat(obj != null && obj.equals(copyObj)).as("Flat query result [" + i + "] does not contain result: '" + id + "': " + obj + "/" + copyObj).isTrue();
                }
                FactHandle fh = row.getFactHandle(id);
                FactHandle copyFh = copyRow.getFactHandle(id);
                if( fh != null ) {
                    assertThat(copyFh).as( "Flat query result [" + i + "] does not contain facthandle: '" + ((InternalFactHandle) fh).getId() + "'").isNotNull();
                    String fhStr = fh.toExternalForm();
                    fhStr = fhStr.substring(0, fhStr.lastIndexOf(":"));
                    String copyFhStr = copyFh.toExternalForm();
                    copyFhStr = copyFhStr.substring(0, copyFhStr.lastIndexOf(":"));
                    assertThat(copyFhStr).as("Unequal fact handles for fact handle '" + ((InternalFactHandle) fh).getId() + "':").isEqualTo(fhStr);
                }
            }
        }

        // check identifiers
        Set<String> copyFlatIds = new TreeSet<String>(Arrays.asList(copyFlatResults.getIdentifiers()));
        assertThat(copyFlatIds.toArray() ).as("Flat query results identifiers").isEqualTo(flatIds.toArray());
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
    public void testQuery2() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Query.drl");
        KieSession session = kbase.newKieSession();

        session.fireAllRules();

        QueryResults results = getQueryResults(session, "assertedobjquery" );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().get("assertedobj")).isEqualTo(new InsertedObject("value1" ));
    }

    @Test
    public void testQueryWithParams() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_QueryWithParams.drl");
        KieSession session = kbase.newKieSession();

        session.fireAllRules();

        String queryName = "assertedobjquery";
        String [] arguments = new String[]{"value1"};
        QueryResultsImpl resultsImpl = (QueryResultsImpl) session.getQueryResults( queryName, arguments );

        QueryResults results = getQueryResults( session, queryName, arguments );

        assertThat(results.size()).isEqualTo(1);
        InsertedObject value = new InsertedObject( "value1" );
        assertThat(results.iterator().next().get("assertedobj")).isEqualTo(value);

        results = getQueryResults( session, "assertedobjquery", new String[]{"value3"}  );

        assertThat(results.size()).isEqualTo(0);

        results = getQueryResults( session, "assertedobjquery2", new String[]{null, "value2"}  );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().get("assertedobj")).isEqualTo(new InsertedObject( "value2" ));

        results = getQueryResults(session, "assertedobjquery2", new String[]{"value3", "value2"}  );

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().get("assertedobj")).isEqualTo(new InsertedObject( "value2" ));
    }

    @Test
    public void testQueryWithMultipleResultsOnKnowledgeApi() throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Cheese \n";
        str += "query cheeses \n";
        str += "    stilton : Cheese(type == 'stilton') \n";
        str += "    cheddar : Cheese(type == 'cheddar', price == stilton.price) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession session = kbase.newKieSession();


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
        assertThat(results.size()).isEqualTo(3);
        assertThat(results.getIdentifiers().length).isEqualTo(2);

        Set newSet = new HashSet();
        for ( org.kie.api.runtime.rule.QueryResultsRow result : results ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ) );
            newSet.add( list );
        }
        assertThat(newSet).isEqualTo(set);

        FlatQueryResults flatResults = new FlatQueryResults( ((StatefulKnowledgeSessionImpl) session).getQueryResults( "cheeses" ) );

        newSet = new HashSet();
        for ( org.kie.api.runtime.rule.QueryResultsRow result : flatResults ) {
            list = new ArrayList();
            list.add( result.get( "stilton" ) );
            list.add( result.get( "cheddar" ) );
            newSet.add( list );
        }
        assertThat(newSet).isEqualTo(set);
    }

    @Test
    public void testTwoQuerries() throws Exception {
        // @see JBRULES-410 More than one Query definition causes an incorrect
        // Rete network to be built.
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_TwoQuerries.drl");
        KieSession session = kbase.newKieSession();

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
        assertThat(results.size()).isEqualTo(1);

        results = getQueryResults( session, "find pensioners" );
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void testDoubleQueryWithExists() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_DoubleQueryWithExists.drl");
        KieSession session = kbase.newKieSession();

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
        assertThat(results.size()).isEqualTo(2);

        // europe=[ 1, 2 ], america=[ 3 ]
        p3.setStatus( "america" );
        session.update( c3FactHandle,
                              p3 );
        session.fireAllRules();
        results = session.getQueryResults(  "2 persons with the same status" );
        assertThat(results.size()).isEqualTo(1);

        // europe=[ 1 ], america=[ 2, 3 ]
        p2.setStatus( "america" );
        session.update( c2FactHandle,
                              p2 );
        session.fireAllRules();
        results = session.getQueryResults( "2 persons with the same status" );
        assertThat(results.size()).isEqualTo(1);

        // europe=[ ], america=[ 1, 2, 3 ]
        p1.setStatus( "america" );
        session.update( c1FactHandle,
                              p1 );
        session.fireAllRules();
        results = getQueryResults( session, "2 persons with the same status" );
        assertThat(results.size()).isEqualTo(2);

        // europe=[ 2 ], america=[ 1, 3 ]
        p2.setStatus( "europe" );
        session.update( c2FactHandle,
                              p2 );
        session.fireAllRules();
        results = getQueryResults( session, "2 persons with the same status" );
        assertThat(results.size()).isEqualTo(1);

        // europe=[ 1, 2 ], america=[ 3 ]
        p1.setStatus( "europe" );
        session.update( c1FactHandle,
                              p1 );
        session.fireAllRules();
        results = session.getQueryResults( "2 persons with the same status" );
        assertThat(results.size()).isEqualTo(1);

        // europe=[ 1, 2, 3 ], america=[ ]
        p3.setStatus( "europe" );
        session.update( c3FactHandle,
                              p3 );
        session.fireAllRules();
        results = session.getQueryResults( "2 persons with the same status" );
        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    public void testQueryWithCollect() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Query.drl");
        KieSession session = kbase.newKieSession();
        session.fireAllRules();

        QueryResults results = getQueryResults( session, "collect objects" );
        assertThat(results.size()).isEqualTo(1);

        final QueryResultsRow row = results.iterator().next();
        final List list = (List) row.get( "$list" );

        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    public void testDroolsQueryCleanup() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_QueryMemoryLeak.drl");

        KieSession ksession = kbase.newKieSession();

        String workerId = "B1234";
        Worker worker = new Worker();
        worker.setId( workerId );

        FactHandle handle = ksession.insert( worker );
        ksession.fireAllRules();

        assertThat(handle).isNotNull();

        Object retractedWorker = null;
        for ( int i = 0; i < 100; i++ ) {
            retractedWorker = ksession.getQueryResults("getWorker",
                                                       new Object[]{workerId});
        }

        assertThat(retractedWorker).isNotNull();

        StatefulKnowledgeSessionImpl sessionImpl = (StatefulKnowledgeSessionImpl) ksession;

        Collection<EntryPointNode> entryPointNodes = sessionImpl.getKnowledgeBase().getRete().getEntryPointNodes().values();

        EntryPointNode defaultEntryPointNode = null;
        for ( EntryPointNode epNode : entryPointNodes ) {
            if ( epNode.getEntryPoint().getEntryPointId().equals( "DEFAULT" ) ) {
                defaultEntryPointNode = epNode;
                break;
            }
        }
        assertThat(defaultEntryPointNode).isNotNull();

        Map<ObjectType, ObjectTypeNode> obnodes = defaultEntryPointNode.getObjectTypeNodes();

        ObjectType key = new ClassObjectType( DroolsQuery.class );
        ObjectTypeNode droolsQueryNode = obnodes.get( key );
        Iterator<InternalFactHandle> it = droolsQueryNode.getFactHandlesIterator(sessionImpl);
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    public void testQueriesWithVariableUnification() throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Person \n";
        str += "query peeps( String $name, String $likes, int $age ) \n";
        str += "    $p : Person( $name := name, $likes := likes, $age := age ) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

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

        QueryResults results = getQueryResults(ksession, "peeps", Variable.v, Variable.v, Variable.v);
        assertThat(results.size()).isEqualTo(4);
        List names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(4);
        assertThat(names.contains("luke")).isTrue();
        assertThat(names.contains("yoda")).isTrue();
        assertThat(names.contains("bobba")).isTrue();
        assertThat(names.contains("darth")).isTrue();

        results = getQueryResults(ksession, "peeps", Variable.v, Variable.v, 300);
        assertThat(results.size()).isEqualTo(3);
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(3);
        assertThat(names.contains("luke")).isTrue();
        assertThat(names.contains("yoda")).isTrue();
        assertThat(names.contains("bobba")).isTrue();

        results = getQueryResults(ksession, "peeps", Variable.v, "stilton", 300);
        assertThat(results.size()).isEqualTo(1);
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(1);
        assertThat(names.contains("yoda")).isTrue();

        results = ksession.getQueryResults("peeps",
                                           Variable.v, "stilton", Variable.v);
        assertThat(results.size()).isEqualTo(2);
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(2);
        assertThat(names.contains("yoda")).isTrue();
        assertThat(names.contains("darth")).isTrue();

        results = ksession.getQueryResults("peeps",
                                           "darth", Variable.v, Variable.v);
        assertThat(results.size()).isEqualTo(1);
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(1);
        assertThat(names.contains("darth")).isTrue();
    }

    @Test
    public void testQueriesWithVariableUnificationOnPatterns() throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Person \n";
        str += "query peeps( Person $p, String $name, String $likes, int $age ) \n";
        str += "    $p := Person( $name := name, $likes := likes, $age := age ) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

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

        org.kie.api.runtime.rule.QueryResults results = ksession.getQueryResults("peeps",
                                                                                 Variable.v, Variable.v, Variable.v, Variable.v);
        assertThat(results.size()).isEqualTo(4);
        List names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(4);
        assertThat(names.contains("luke")).isTrue();
        assertThat(names.contains("yoda")).isTrue();
        assertThat(names.contains("bobba")).isTrue();
        assertThat(names.contains("darth")).isTrue();

        results = ksession.getQueryResults("peeps",
                                           p1, Variable.v, Variable.v, Variable.v);
        assertThat(results.size()).isEqualTo(1);
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(1);
        assertThat(names.contains("darth")).isTrue();
    }

    @Test
    public void testQueriesWithVariableUnificationOnNestedFields() throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Person \n";
        str += "query peeps( String $name, String $likes, String $street) \n";
        str += "    $p : Person( $name := name, $likes := likes, $street := address.street ) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

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

        QueryResults results = getQueryResults(ksession, "peeps", Variable.v, Variable.v, Variable.v);
        assertThat(results.size()).isEqualTo(2);
        List names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.contains("yoda")).isTrue();
        assertThat(names.contains("darth")).isTrue();

        results = getQueryResults(ksession, "peeps", Variable.v, Variable.v, "s1");
        assertThat(results.size()).isEqualTo(1);
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.contains("darth")).isTrue();
    }

    @Test
    public void testOpenQuery() throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Cheese \n";
        str += "query cheeses(String $type1, String $type2) \n";
        str += "    stilton : Cheese(type == $type1, $sprice : price) \n";
        str += "    cheddar : Cheese(type == $type2, $cprice : price == stilton.price) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

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
        assertThat(added.size()).isEqualTo(3);
        assertThat(removed.size()).isEqualTo(0);
        assertThat(updated.size()).isEqualTo(0);

        // Assert that the identifiers where retrievable
        assertThat(added.get(2)[0]).isSameAs(stilton1);
        assertThat(added.get(2)[1]).isSameAs(cheddar1);
        assertThat(added.get(2)[2]).isEqualTo(1);
        assertThat(added.get(2)[3]).isEqualTo(1);
        assertThat(added.get(2)[4]).isEqualTo("stilton");
        assertThat(added.get(2)[5]).isEqualTo("cheddar");

        // And that we have correct values from those rows
        assertThat(added.get(0)[3]).isEqualTo(3);
        assertThat(added.get(1)[3]).isEqualTo(2);
        assertThat(added.get(2)[3]).isEqualTo(1);

        // Do an update that causes a match to become untrue, thus triggering a removed
        cheddar3.setPrice( 4 );
        ksession.update( c3Fh,
                         cheddar3 );
        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(3);
        assertThat(removed.size()).isEqualTo(1);
        assertThat(updated.size()).isEqualTo(0);

        assertThat(removed.get(0)[3]).isEqualTo(4);

        // Now make that partial true again, and thus another added
        cheddar3.setPrice( 3 );
        ksession.update( c3Fh,
                         cheddar3 );
        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(1);
        assertThat(updated.size()).isEqualTo(0);

        assertThat(added.get(3)[3]).isEqualTo(3);

        // check a standard update
        cheddar3.setOldPrice( 0 );
        ksession.update( c3Fh,
                         cheddar3 );
        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(1);
        assertThat(updated.size()).isEqualTo(1);

        assertThat(updated.get(0)[3]).isEqualTo(3);

        // Check a standard retract
        ksession.retract( s1Fh );
        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(2);
        assertThat(updated.size()).isEqualTo(1);

        assertThat(removed.get(1)[3]).isEqualTo(1);

        // Close the query, we should get removed events for each row
        query.close();

        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(4);
        assertThat(updated.size()).isEqualTo(1);

        assertThat(removed.get(3)[3]).isEqualTo(2);
        assertThat(removed.get(2)[3]).isEqualTo(3);

        // Check that updates no longer have any impact.
        ksession.update( c3Fh,
                         cheddar3 );
        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(4);
        assertThat(updated.size()).isEqualTo(1);
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
        str += "package org.drools.mvel.integrationtests\n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "query cheeses(String $type) \n";
        str += "    $cheese : Cheese(type == $type) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        // insert some data into the session
        for ( int i = 0; i < 10000; i++ ) {
            ksession.insert( new Cheese( i % 2 == 0 ? "stilton" : "brie" ) );
        }

        // query the session
        List<Cheese> cheeses;
        for ( int i = 0; i < 100; i++ ) {
            org.kie.api.runtime.rule.QueryResults queryResults = ksession.getQueryResults( "cheeses",
                                                                                           "stilton");
            cheeses = new ArrayList<Cheese>();
            for ( QueryResultsRow row : queryResults ) {
                cheeses.add( (Cheese) row.get( "$cheese" ) );
            }

            assertThat(cheeses.size()).isEqualTo(5000);
        }
    }

    @Test
    public void testQueryWithEval() throws IOException, ClassNotFoundException {
        // [Regression in 5.2.0.M2]: NPE during rule evaluation on MVELPredicateExpression.evaluate(MVELPredicateExpression.java:82)

        String str = "package org.drools.mvel.integrationtests\n" +
                     "import " + DomainObject.class.getCanonicalName() + " \n" +
                     "query queryWithEval \n" +
                     "    $do: DomainObject()\n" +
                     "    not DomainObject( id == $do.id, eval(interval.isAfter($do.getInterval())))\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().get("$do")).isEqualTo(do2);

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

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.size()).isEqualTo(2);
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

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.size()).isEqualTo(1);
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

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.size()).isEqualTo(1);
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ks = kbase.newKieSession();

        ArrayList list = new ArrayList();
        ks.setGlobal( "AString", "Hello" );
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertThat(list).isEqualTo(List.of("Hello World"));
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
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ks = kbase.newKieSession();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertThat(list).isEqualTo(Arrays.asList("aa", "bb"));
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ks = kbase.newKieSession();

        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.setGlobal( "number", 3 );

        ks.insert( "Hi" );
        ks.insert( "Bye" );
        ks.insert( "Hello" );
        ks.fireAllRules();

        assertThat(list).isEqualTo(List.of("Bye"));
    }

    @Test
    public void testQueryWithAccessorAsArgument() throws Exception {
        // DROOLS-414
        String str =
                "import org.drools.mvel.compiler.Person\n" +
                "global java.util.List persons;\n" +
                "\n" +
                "query contains(String $s, String $c)\n" +
                "    $s := String( this.contains( $c ) )\n" +
                "end\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person()\n" +
                "    contains( $p.name, \"a\"; )\n" +
                "then\n" +
                "    persons.add( $p );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Person> personsWithA = new ArrayList<Person>();
        ksession.setGlobal("persons", personsWithA);

        ksession.insert("Mark");
        ksession.insert("Edson");
        ksession.insert("Mario");
        ksession.insert(new Person("Mark"));
        ksession.insert(new Person("Edson"));
        ksession.insert(new Person("Mario"));
        ksession.fireAllRules();

        assertThat(personsWithA.size()).isEqualTo(2);
        for (Person p : personsWithA) {
            assertThat(p.getName().equals("Mark") || p.getName().equals("Mario")).isTrue();
        }
    }

    @Test
    public void testQueryWithExpressionAsArgument() throws Exception {
        // DROOLS-414
        String str =
                "import org.drools.mvel.compiler.Person\n" +
                "global java.util.List persons;\n" +
                "\n" +
                "query checkLength(String $s, int $l)\n" +
                "    $s := String( length == $l )\n" +
                "end\n" +
                "\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    $p : Person()\n" +
                "    checkLength( $p.name, 1 + $i + $p.age; )\n" +
                "then\n" +
                "    persons.add( $p );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Person> list = new ArrayList<Person>();
        ksession.setGlobal("persons", list);

        ksession.insert(1);
        ksession.insert("Mark");
        ksession.insert("Edson");
        ksession.insert("Mario");
        ksession.insert(new Person("Mark", 2));
        ksession.insert(new Person("Edson", 3));
        ksession.insert(new Person("Mario", 4));
        ksession.fireAllRules();

        System.out.println(list);
        assertThat(list.size()).isEqualTo(2);
        for (Person p : list) {
            assertThat(p.getName().equals("Mark") || p.getName().equals("Edson")).isTrue();
        }
    }

    @Test
    public void testNotExistingDeclarationInQuery() {
        // DROOLS-414
        String drl =
                "import org.drools.compiler.Person\n" +
                "global java.util.List persons;\n" +
                "\n" +
                "query checkLength(String $s, int $l)\n" +
                "    $s := String( length == $l )\n" +
                "end\n" +
                "\n" +
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    $p : Person()\n" +
                "    checkLength( $p.name, 1 + $x + $p.age; )\n" +
                "then\n" +
                "    persons.add( $p );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testQueryInSubnetwork() {
        // DROOLS-1386
        String str = "query myquery(Integer $i)\n" +
                     "   $i := Integer()\n" +
                     "end\n" +
                     "\n" +
                     "rule R when\n" +
                     "   String()\n" +
                     "   accumulate (myquery($i;);\n" +
                     "      $result_count : count(1)\n" +
                     "   )\n" +
                     "   eval($result_count > 0)\n" +
                     "then\n" +
                     "end\n\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        FactHandle iFH = ksession.insert( 1 );
        FactHandle sFH = ksession.insert( "" );

        ksession.fireAllRules();

        ksession.update( iFH, 1 );
        ksession.delete( sFH );

        ksession.fireAllRules();
    }

    @Test
    public void testOpenQueryNoParams() throws Exception {
        // RHDM-717
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Cheese \n";
        str += "query cheeses \n";
        str += "    stilton : Cheese(type == 'stilton') \n";
        str += "    cheddar : Cheese(type == 'cheddar', price == stilton.price) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Cheese stilton1 = new Cheese( "stilton", 1 );
        Cheese cheddar1 = new Cheese( "cheddar", 1 );
        Cheese stilton2 = new Cheese( "stilton", 2 );
        Cheese cheddar2 = new Cheese( "cheddar", 2 );
        Cheese stilton3 = new Cheese( "stilton", 3 );
        Cheese cheddar3 = new Cheese( "cheddar", 3 );

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
                Object[] array = new Object[2];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                updated.add( array );
            }

            public void rowDeleted( Row row ) {
                Object[] array = new Object[2];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                removed.add( array );
            }

            public void rowInserted( Row row ) {
                Object[] array = new Object[2];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                added.add( array );
            }
        };

        // Open the LiveQuery
        LiveQuery query = ksession.openLiveQuery( "cheeses",null, listener );

        ksession.fireAllRules();

        // Assert that on opening we have three rows added
        assertThat(added.size()).isEqualTo(3);
        assertThat(removed.size()).isEqualTo(0);
        assertThat(updated.size()).isEqualTo(0);

        // Do an update that causes a match to become untrue, thus triggering a removed
        cheddar3.setPrice( 4 );
        ksession.update( c3Fh, cheddar3 );
        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(3);
        assertThat(removed.size()).isEqualTo(1);
        assertThat(updated.size()).isEqualTo(0);

        // Now make that partial true again, and thus another added
        cheddar3.setPrice( 3 );
        ksession.update( c3Fh, cheddar3 );
        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(1);
        assertThat(updated.size()).isEqualTo(0);

        // check a standard update
        cheddar3.setOldPrice( 0 );
        ksession.update( c3Fh, cheddar3 );
        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(1);
        assertThat(updated.size()).isEqualTo(1);

        // Check a standard retract
        ksession.retract( s1Fh );
        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(2);
        assertThat(updated.size()).isEqualTo(1);

        // Close the query, we should get removed events for each row
        query.close();

        ksession.fireAllRules();

        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(4);
        assertThat(updated.size()).isEqualTo(1);

        // Check that updates no longer have any impact.
        ksession.update( c3Fh, cheddar3 );
        assertThat(added.size()).isEqualTo(4);
        assertThat(removed.size()).isEqualTo(4);
        assertThat(updated.size()).isEqualTo(1);
    }

    public static class Question {}
    public static class QuestionVisible {
        private final Question question;
        public QuestionVisible( Question question ) {
            this.question = question;
        }
        public Question getQuestion() {
            return question;
        }
    }

    @Test
    public void testQueryWithOptionalOr() {
        // DROOLS-1386
        String str =
                "package org.test\n" +
                "import " + Question.class.getCanonicalName() + "\n" +
                "import " + QuestionVisible.class.getCanonicalName() + "\n" +
                "query QuestionsKnowledge\n" +
                "    $question: Question()\n" +
                "    $visible: QuestionVisible(question == $question) or not QuestionVisible(question == $question)\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        Question question = new Question();
        ksession.insert( question );
        QueryResults results = ksession.getQueryResults("QuestionsKnowledge");
        assertThat(results.size()).isEqualTo(1);
        QueryResultsRow row = results.iterator().next();
        assertThat(row.get("$question")).isSameAs(question);

        QuestionVisible questionVisible = new QuestionVisible( question );
        ksession.insert( questionVisible );
        results = ksession.getQueryResults("QuestionsKnowledge");
        assertThat(results.size()).isEqualTo(1);
        row = results.iterator().next();
        assertThat(row.get("$question")).isSameAs(question);
        assertThat(row.get("$visible")).isSameAs(questionVisible);
    }

    @Test
    public void testQueryWithFrom() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.Thing;\n" +
                "query isContainedIn( Thing $x, Thing $y )\n" +
                "    $y := Thing() from $x.children\n" +
                "or\n" +
                "    ( $z := Thing() from $x.children and isContainedIn( $z, $y; ) )\n" +
                "end\n";

        final Thing smartphone = new Thing("smartphone");
        final List<String> itemList = Arrays.asList("display", "keyboard", "processor");
        itemList.stream().map(item -> new Thing(item)).forEach((thing) -> smartphone.addChild(thing));

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        ReteDumper.dumpRete(ksession);

        ksession.insert(smartphone);

        final QueryResults queryResults = ksession.getQueryResults("isContainedIn", smartphone, Variable.v);
        final List<String> resultList = StreamSupport.stream(queryResults.spliterator(), false)
                .map(row -> ((Thing) row.get("$y")).getName()).collect(Collectors.toList());
        assertThat(resultList).as("Query does not contain all items").containsAll(itemList);

        ksession.dispose();
    }
}
