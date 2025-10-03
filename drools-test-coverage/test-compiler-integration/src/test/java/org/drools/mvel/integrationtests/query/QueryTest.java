/*
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
package org.drools.mvel.integrationtests.query;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.DroolsQuery;
import org.drools.base.base.ObjectType;
import org.drools.commands.runtime.FlatQueryResults;
import org.drools.core.QueryResultsImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.DomainObject;
import org.drools.mvel.compiler.InsertedObject;
import org.drools.mvel.compiler.Interval;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.Worker;
import org.drools.mvel.compiler.oopath.model.Thing;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;

import jakarta.xml.bind.JAXBContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class QueryTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    private static QueryResults getQueryResults(KieSession session, String queryName, Object... arguments) throws Exception {
        QueryResultsImpl results = (QueryResultsImpl) session.getQueryResults(queryName, arguments);

        FlatQueryResults flatResults = new FlatQueryResults(results);

        assertThat(flatResults).as("Query results size").hasSameSizeAs(results);
        assertThat(flatResults.getIdentifiers()).as("Query results identifiers").hasSameSizeAs(results.getIdentifiers());
        Set<String> resultIds = new TreeSet<String>(Arrays.asList(results.getIdentifiers()));
        Set<String> flatIds = new TreeSet<String>(Arrays.asList(flatResults.getIdentifiers()));
        assertThat(resultIds.toArray()).as("Flat query results identifiers").isEqualTo(flatIds.toArray());

        FlatQueryResults copyFlatResults = roundTrip(flatResults);
        String [] identifiers = results.getIdentifiers();
        Iterator<QueryResultsRow> copyFlatIter = copyFlatResults.iterator();
        for(int i = 0; i < results.size(); ++i) {
            QueryResultsRow row = results.get(i);
            assertThat(copyFlatIter.hasNext()).as("Round-tripped flat query results contain less rows than original query results").isTrue();
            QueryResultsRow copyRow = copyFlatIter.next();
            for(String id : identifiers) {
                Object obj = row.get(id);
                if(obj != null) {
                    Object copyObj = copyRow.get(id);
                    assertThat(obj != null && obj.equals(copyObj)).as("Flat query result [" + i + "] does not contain result: '" + id + "': " + obj + "/" + copyObj).isTrue();
                }
                FactHandle fh = row.getFactHandle(id);
                FactHandle copyFh = copyRow.getFactHandle(id);
                if(fh != null) {
                    assertThat(copyFh).as("Flat query result [" + i + "] does not contain facthandle: '" + fh.getId() + "'").isNotNull();
                    String fhStr = fh.toExternalForm();
                    fhStr = fhStr.substring(0, fhStr.lastIndexOf(":"));
                    String copyFhStr = copyFh.toExternalForm();
                    copyFhStr = copyFhStr.substring(0, copyFhStr.lastIndexOf(":"));
                    assertThat(copyFhStr).as("Unequal fact handles for fact handle '" + fh.getId() + "':").isEqualTo(fhStr);
                }
            }
        }

        // check identifiers
        Set<String> copyFlatIds = new TreeSet<String>(Arrays.asList(copyFlatResults.getIdentifiers()));
        assertThat(copyFlatIds.toArray()).as("Flat query results identifiers").isEqualTo(flatIds.toArray());
        return copyFlatResults;
    }


    private static <T> T roundTrip(Object obj) throws Exception {
        Class[] classes = { obj.getClass() };
        JAXBContext ctx = getJaxbContext(classes);
        String xmlOut = marshall(ctx, obj);
        return unmarshall(ctx, xmlOut);
    }

    private static <T> T unmarshall(JAXBContext ctx, String xmlIn) throws Exception {
        ByteArrayInputStream xmlStrInputStream = new ByteArrayInputStream(xmlIn.getBytes(Charset.forName("UTF-8")));
        Object out = ctx.createUnmarshaller().unmarshal(xmlStrInputStream);
        return (T) out;
    }

    private static String marshall(JAXBContext ctx, Object obj) throws Exception {
        StringWriter writer = new StringWriter();
        ctx.createMarshaller().marshal(obj, writer);
        return writer.getBuffer().toString();
    }

    private static JAXBContext getJaxbContext(Class<?>... classes) throws Exception {
        List<Class<?>> jaxbClassList = new ArrayList<Class<?>>();
        jaxbClassList.addAll(Arrays.asList(classes));
        jaxbClassList.add(Cheese.class);
        jaxbClassList.add(InsertedObject.class);
        jaxbClassList.add(Person.class);
        Class<?>[] jaxbClasses = jaxbClassList.toArray(new Class[jaxbClassList.size()]);
        return JAXBContext.newInstance(jaxbClasses);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQuery2(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
    	String drl = """
			package org.drools.compiler.integrationtests;
			
			import org.drools.mvel.compiler.InsertedObject;
			import java.util.ArrayList;
			
			rule rule1
			  when
			  then
			    insert( new InsertedObject( "value1") );
			    insert( new InsertedObject( "value2") );
			end
			
			query "assertedobjquery"
			    assertedobj : InsertedObject( value=="value1" )
			end 
			
			query "collect objects"
			    $list : ArrayList() from collect( InsertedObject() )
			end    			
    		""";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession session = kbase.newKieSession();

        session.fireAllRules();

        QueryResults results = getQueryResults(session, "assertedobjquery");
        assertThat(results).hasSize(1);
        assertThat(results.iterator().next().get("assertedobj")).isEqualTo(new InsertedObject("value1"));
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithParams(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
    	String drl = """
			package org.drools.integrationtests;
			
			import org.drools.mvel.compiler.InsertedObject;
			
			rule rule1
			  when
			  then
			    insert( new InsertedObject( "value1") );
			    insert( new InsertedObject( "value2") );
			end
			
			query "assertedobjquery" ( String $value )
			    assertedobj : InsertedObject( value == $value )
			end 
			
			
			query "assertedobjquery2" ( String $value1, String $value2 )
			    assertedobj : InsertedObject( value == $value2 )
			end    			
    		""";
    	
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession session = kbase.newKieSession();

        session.fireAllRules();

        QueryResults results = getQueryResults(session, "assertedobjquery", "value1");

        assertThat(results).hasSize(1);
        InsertedObject value = new InsertedObject("value1");
        assertThat(results.iterator().next().get("assertedobj")).isEqualTo(value);

        results = getQueryResults(session, "assertedobjquery", "value3");

        assertThat(results).hasSize(0);

        results = getQueryResults(session, "assertedobjquery2", null, "value2");
        assertThat(results).hasSize(1);
        assertThat(results.iterator().next().get("assertedobj")).isEqualTo(new InsertedObject("value2"));

        results = getQueryResults(session, "assertedobjquery2", "value3", "value2");

        assertThat(results).hasSize(1);
        assertThat(results.iterator().next().get("assertedobj")).isEqualTo(new InsertedObject("value2"));
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithMultipleResultsOnKnowledgeApi(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String drl = """
        	package org.drools.mvel.integrationtests;
        	import org.drools.mvel.compiler.Cheese;
        	query cheeses
        		stilton : Cheese(type == 'stilton')
        		cheddar : Cheese(type == 'cheddar', price == stilton.price)
        	end
        	""";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession session = kbase.newKieSession();


        Cheese stilton1 = new Cheese("stilton", 1);
        Cheese cheddar1 = new Cheese("cheddar", 1);
        Cheese stilton2 = new Cheese("stilton", 2);
        Cheese cheddar2 = new Cheese("cheddar", 2);
        Cheese stilton3 = new Cheese("stilton", 3);
        Cheese cheddar3 = new Cheese("cheddar", 3);

        session.insert(stilton1);
        session.insert(stilton2);
        session.insert(stilton3);
        session.insert(cheddar1);
        session.insert(cheddar2);
        session.insert(cheddar3);

        QueryResults results = getQueryResults(session, "cheeses");
        assertThat(results.getIdentifiers()).hasSize(2);
        
        assertThat(results).hasSize(3);
        assertThat(results)
    		.extracting(r->r.get("stilton"), r-> r.get("cheddar"))
    		.containsExactlyInAnyOrder(
    			tuple(stilton1, cheddar1),
    			tuple(stilton2, cheddar2),
    			tuple(stilton3, cheddar3));

        FlatQueryResults flatResults = new FlatQueryResults(((StatefulKnowledgeSessionImpl) session).getQueryResults("cheeses"));

        assertThat(flatResults)
        	.extracting(r->r.get("stilton"), r-> r.get("cheddar"))
        	.containsExactlyInAnyOrder(
        			tuple(stilton1, cheddar1),
        			tuple(stilton2, cheddar2),
        			tuple(stilton3, cheddar3));
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testTwoQueries(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // @see JBRULES-410 More than one Query definition causes an incorrect
        // Rete network to be built.
    	
    	String drl = """
			package org.drools.compiler.test;

			import org.drools.mvel.compiler.Cheese;
			import org.drools.mvel.compiler.Person;

			query "find stinky cheeses"
				Cheese(type == "stinky")
			end 

			query "find pensioners"
				Person(age > 65)
			end 
			""";
    	
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession session = kbase.newKieSession();

        final Cheese stilton = new Cheese("stinky", 5);
        session.insert(stilton);
        final Person per1 = new Person("stinker", "smelly feet", 70);
        final Person per2 = new Person("skunky", "smelly armpits", 40);
        session.insert(per1);
        session.insert(per2);

        assertThat(getQueryResults(session, "find stinky cheeses")).hasSize(1);
        assertThat(getQueryResults(session, "find pensioners")).hasSize(1);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDoubleQueryWithExists(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
    	String drl = """
    		package org.drools.mvel.integrationtests;
    		import org.drools.mvel.compiler.Person;
			query "2 persons with the same status"
				p : Person($status : status, $age : age)
				exists Person(status == $status, age > $age);
			end
   			""";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession session = kbase.newKieSession();

        
        final Person stilton20 = new Person("p1", "stilton", 20);
        stilton20.setStatus("europe");
        final FactHandle c1FactHandle = session.insert(stilton20);
        
        final Person stilton30 = new Person("p2", "stilton", 30);
        stilton30.setStatus("europe");
        final FactHandle c2FactHandle = session.insert(stilton30);
        
        final Person stilton40 = new Person("p3", "stilton", 40);
        stilton40.setStatus("europe");
        final FactHandle c3FactHandle = session.insert(stilton40);
        

        QueryResults results = session.getQueryResults("2 persons with the same status");
        assertThat(results).hasSize(2);

        // europe=[ 1, 2 ], america=[ 3 ]
        stilton40.setStatus("america");
        session.update(c3FactHandle, stilton40);
        
        results = session.getQueryResults( "2 persons with the same status");
        assertThat(results).hasSize(1);

        // europe=[ 1 ], america=[ 2, 3 ]
        stilton30.setStatus("america");
        session.update(c2FactHandle, stilton30);
 
        results = session.getQueryResults("2 persons with the same status");
        assertThat(results).hasSize(1);

        // europe=[ ], america=[ 1, 2, 3 ]
        stilton20.setStatus("america");
        session.update(c1FactHandle, stilton20);

        results = getQueryResults(session, "2 persons with the same status");
        assertThat(results).hasSize(2);

        // europe=[ 2 ], america=[ 1, 3 ]
        stilton30.setStatus("europe");
        session.update(c2FactHandle, stilton30);

        results = getQueryResults(session, "2 persons with the same status");
        assertThat(results).hasSize(1);

        // europe=[ 1, 2 ], america=[ 3 ]
        stilton20.setStatus("europe");
        session.update(c1FactHandle, stilton20);

        results = session.getQueryResults("2 persons with the same status");
        assertThat(results).hasSize(1);

        // europe=[ 1, 2, 3 ], america=[ ]
        stilton40.setStatus("europe");
        session.update(c3FactHandle, stilton40);

        results = session.getQueryResults("2 persons with the same status");
        assertThat(results).hasSize(2);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithCollect(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
    	String drl = """
			package org.drools.compiler.integrationtests;
			
			import org.drools.mvel.compiler.InsertedObject;
			import java.util.ArrayList;
			
			rule rule1
			  when
			  then
			    insert( new InsertedObject( "value1") );
			    insert( new InsertedObject( "value2") );
			end
			
			query "assertedobjquery"
			    assertedobj : InsertedObject( value=="value1" )
			end 
			
			query "collect objects"
			    $list : ArrayList() from collect( InsertedObject() )
			end    			
    			""";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession session = kbase.newKieSession();
        session.fireAllRules();

        QueryResults results = getQueryResults(session, "collect objects");
        assertThat(results).hasSize(1);

        final QueryResultsRow row = results.iterator().next();
        final List list = (List) row.get("$list");

        assertThat(list).hasSize(2);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDroolsQueryCleanup(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
    	String drl = """
			package org.drools.mvel.compiler
			
			query getWorker(String _id)
			    queryResult : Worker(id == _id)
			end
			
			query getWorkers()
			    queryResult : Worker()
			end    			
    		""";
    	
    	
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        KieSession ksession = kbase.newKieSession();

        String workerId = "B1234";
        Worker worker = new Worker();
        worker.setId(workerId);

        FactHandle handle = ksession.insert(worker);
        ksession.fireAllRules();

        assertThat(handle).isNotNull();

        Object retractedWorker = null;
        for (int i = 0; i < 100; i++) {
            retractedWorker = ksession.getQueryResults("getWorker",
                                                       new Object[]{workerId});
        }

        assertThat(retractedWorker).isNotNull();

        StatefulKnowledgeSessionImpl sessionImpl = (StatefulKnowledgeSessionImpl) ksession;

        Collection<EntryPointNode> entryPointNodes = ((InternalRuleBase)kbase).getRete().getEntryPointNodes().values();

        EntryPointNode defaultEntryPointNode = null;
        for (EntryPointNode epNode : entryPointNodes) {
            if (epNode.getEntryPoint().getEntryPointId().equals("DEFAULT")) {
                defaultEntryPointNode = epNode;
                break;
            }
        }
        assertThat(defaultEntryPointNode).isNotNull();

        Map<ObjectType, ObjectTypeNode> obnodes = defaultEntryPointNode.getObjectTypeNodes();

        ObjectType key = new ClassObjectType(DroolsQuery.class);
        ObjectTypeNode droolsQueryNode = obnodes.get(key);
        Iterator<InternalFactHandle> it = droolsQueryNode.getFactHandlesIterator(sessionImpl);
        assertThat(it).isExhausted();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testStandardQueryListener(KieBaseTestConfiguration kieBaseTestConfiguration) throws IOException, ClassNotFoundException {
        runQueryListenerTest(kieBaseTestConfiguration, QueryListenerOption.STANDARD);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNonCloningQueryListener(KieBaseTestConfiguration kieBaseTestConfiguration) throws IOException, ClassNotFoundException {
        runQueryListenerTest(kieBaseTestConfiguration, QueryListenerOption.LIGHTWEIGHT);
    }

    public void runQueryListenerTest(KieBaseTestConfiguration kieBaseTestConfiguration , QueryListenerOption option) throws IOException, ClassNotFoundException {
        String drl = """
    		package org.drools.mvel.integrationtests;
    		import org.drools.mvel.compiler.Cheese;
    		query cheeses(String $type)
    			$cheese : Cheese(type == $type)
    		end
    
    		""";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        // insert some data into the session
        for (int i = 0; i < 10000; i++) {
            ksession.insert(new Cheese(i % 2 == 0 ? "stilton" : "brie"));
        }

        // query the session
        for (int i = 0; i < 100; i++) {
            QueryResults queryResults = ksession.getQueryResults("cheeses", "stilton");
            
            assertThat(queryResults).extracting(row -> row.get("$cheese")).hasSize(5000);
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithEval(KieBaseTestConfiguration kieBaseTestConfiguration) throws IOException, ClassNotFoundException {
        // [Regression in 5.2.0.M2]: NPE during rule evaluation on MVELPredicateExpression.evaluate(MVELPredicateExpression.java:82)

        String drl = """
        	package org.drools.mvel.integrationtests
        	import org.drools.mvel.compiler.DomainObject;
        	query queryWithEval
        		$do: DomainObject()
        		not DomainObject(id == $do.id, eval(interval.isAfter($do.getInterval())))
        	end
        	""";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        DomainObject do1 = new DomainObject();
        do1.setId(1);
        do1.setInterval(new Interval(10, 5));
        DomainObject do2 = new DomainObject();
        do2.setId(1);
        do2.setInterval(new Interval(20, 5));
        ksession.insert(do1);
        ksession.insert(do2);

        QueryResults results = ksession.getQueryResults("queryWithEval");
        assertThat(results).hasSize(1);
        assertThat(results.iterator().next().get("$do")).isEqualTo(do2);
    }




    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testGlobalsInQueries(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl = """
		    package com.sample
		
		    global java.lang.String AString;
		    global java.util.List list;
		
		    declare AThing
		         name: String @key
		    end
		
		    rule init
		         when
		         then
		             insert(new AThing(AString));
		             insert(new AThing('Holla'));
		    end
		
		    query test(String $in) 
		         AThing($in;)
		    end
		
		    rule spot
		         when
		             test("Hello";)
		             AThing("Hello";)
		             test(AString;)
		             AThing(AString;)
		         then
		             list.add(AString + " World");
		    end
		    """;

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ks = kbase.newKieSession();

        List<String> list = new ArrayList<>();
        ks.setGlobal("AString", "Hello");
        ks.setGlobal("list", list);
        ks.fireAllRules();

        assertThat(list).containsExactly("Hello World");
    }


    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithClassArg(KieBaseTestConfiguration kieBaseTestConfiguration) {
        //DROOLS-590
        String drl = """
    	    global java.util.List list;

    	    declare Foo end

    	    query bar(Class $c)
    	      Class(this.getName() == $c.getName())
    	    end

    	    query bar2(Class $c)
    	      Class(this == $c)
    	    end

    	    rule Init when then insert(Foo.class); end

    	    rule React1
    	    when
    	      bar(Foo.class ;)
    	    then
    	      list.add('aa');
    	    end

    	    rule React2
    	    when
    	      bar2(Foo.class ;)
    	    then
    	      list.add('bb');
    	    end
    	    """;

        List<String> list = new ArrayList<>();
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ks = kbase.newKieSession();
        ks.setGlobal("list", list);
        ks.fireAllRules();

        assertThat(list).containsExactly("aa", "bb");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testPassGlobalToNestedQuery(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-851
        String drl = """
		    global java.util.List list;
		    global Integer number;
		
		    query findString(String $out)
		        findStringWithLength(number, $out;)
		    end
		
		    query findStringWithLength(int $in, String $out)
		        $out := String($in := length)
		    end
		
		    rule R when
		        findString($s;)
		    then
		        list.add($s);
		    end
		    """;

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ks = kbase.newKieSession();

        List<String> list = new ArrayList<>();
        ks.setGlobal("list", list);
        ks.setGlobal("number", 3);

        ks.insert("Hi");
        ks.insert("Bye");
        ks.insert("Hello");
        ks.fireAllRules();

        assertThat(list).containsExactly("Bye");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithAccessorAsArgument(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // DROOLS-414
        String drl = """
        	import org.drools.mvel.compiler.Person
        	global java.util.List persons;
        	query contains(String $s, String $c)
        		$s := String(this.contains($c))
        	end
        	rule R when
        		$p : Person()
        		contains($p.name, "a";)
        	then
        		persons.add($p);
        	end
        	""";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
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

        assertThat(personsWithA).hasSize(2);
        assertThat(personsWithA).extracting(p -> p.getName()).containsExactlyInAnyOrder("Mark", "Mario");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithExpressionAsArgument(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // DROOLS-414
        String drl = """
    		import org.drools.mvel.compiler.Person;
    		global java.util.List persons;
    		query checkLength(String $s, int $l)
    			$s := String(length == $l)
    		end
    		rule R when
    			$i : Integer()
    			$p : Person()
    			checkLength($p.name, 1 + $i + $p.age;)
    		then
    			persons.add($p);
    		end
    		""";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
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

        assertThat(list).hasSize(2);
        
        assertThat(list).extracting(p -> p.getName()).containsExactlyInAnyOrder("Mark", "Edson");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryInSubnetwork(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-1386
        String drl = """
			query myquery(Integer $i)
			   $i := Integer()
			end
			
			rule R when
			   String()
			   accumulate (myquery($i;);
			      $result_count : count(1)
			   )
			   eval($result_count > 0)
			then
			end
			""";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        FactHandle iFH = ksession.insert(1);
        FactHandle sFH = ksession.insert("");

        ksession.fireAllRules();

        ksession.update(iFH, 1);
        ksession.delete(sFH);

        ksession.fireAllRules();
    }

    public static class Question {}
    public static class QuestionVisible {
        private final Question question;
        public QuestionVisible(Question question) {
            this.question = question;
        }
        public Question getQuestion() {
            return question;
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithOptionalOr(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-1386
        String drl = """
        	package org.test;
        	import org.drools.mvel.integrationtests.query.QueryTest.Question;
        	import org.drools.mvel.integrationtests.query.QueryTest.QuestionVisible;
        	query QuestionsKnowledge
        		$question: Question()
        		$visible: QuestionVisible(question == $question) or not QuestionVisible(question == $question)
        	end
        	""";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        Question question = new Question();
        ksession.insert(question);
        QueryResults results = ksession.getQueryResults("QuestionsKnowledge");
        assertThat(results).hasSize(1);
        QueryResultsRow row = results.iterator().next();
        assertThat(row.get("$question")).isSameAs(question);

        QuestionVisible questionVisible = new QuestionVisible(question);
        ksession.insert(questionVisible);
        results = ksession.getQueryResults("QuestionsKnowledge");
        assertThat(results).hasSize(1);
        row = results.iterator().next();
        assertThat(row.get("$question")).isSameAs(question);
        assertThat(row.get("$visible")).isSameAs(questionVisible);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithFrom(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl = """
        	import org.drools.mvel.compiler.oopath.model.Thing;
            query isContainedIn(Thing $x, Thing $y)
        		$y := Thing() from $x.children
        		or
        		($z := Thing() from $x.children and isContainedIn($z, $y;))
        	end
        	""";

        Thing smartphone = new Thing("smartphone");
        List<String> itemList = Arrays.asList("display", "keyboard", "processor");
        itemList.stream().map(item -> new Thing(item)).forEach((thing) -> smartphone.addChild(thing));

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(smartphone);

        QueryResults queryResults = ksession.getQueryResults("isContainedIn", smartphone, Variable.v);
        assertThat(queryResults).as("Query does not contain all items").extracting(row -> ((Thing) row.get("$y")).getName()).containsAll(itemList);
    }
}
