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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.integrationtests.incrementalcompilation.TestUtil;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.SerializationHelper;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.api.runtime.rule.Variable.v;

@RunWith(Parameterized.class)
public class BackwardChainingTest extends AbstractBackwardChainingTest {

    public BackwardChainingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test(timeout = 10000)
    public void testQueryPatternBindingAsResult() throws IOException, ClassNotFoundException {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "global java.util.List list\n" +
                     "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
                     "    $p := Person( $name := name, $likes := likes, $age := age; ) \n" +
                     "end\n";

        str += "rule x1\n" +
               "when\n" +
               "    String( this == \"go1\" )\n" +
               //     output, output, output          ,output
               "    ?peeps($p, $name1; $likes1 : $likes, $age1 : $age )\n" +
               "then\n" +
               "   list.add( $p );\n" +
               "   list.add( $name1 + \" : \" + $age1 );\n" +
               "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p1 = new Person("darth",
                                         "stilton",
                                         100);
            final Person p2 = new Person("darth",
                                         "stilton",
                                         200);
            final Person p3 = new Person("yoda",
                                         "stilton",
                                         300);
            final Person p4 = new Person("luke",
                                         "brie",
                                         300);
            final Person p5 = new Person("bobba",
                                         "cheddar",
                                         300);

            ksession.insert(p1);
            ksession.insert(p2);
            ksession.insert(p3);
            ksession.insert(p4);
            ksession.insert(p5);

            ksession.insert("go1");
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            if (kieBaseTestConfiguration.isIdentity()) {
                assertEquals(10, list.size());
                assertEquals(p1, list.get(list.indexOf("darth : 100") - 1));
                assertTrue(list.contains("darth : 100"));
                assertEquals(p2, list.get(list.indexOf("darth : 200") - 1));
                assertTrue(list.contains("darth : 200"));
                assertEquals(p3, list.get(list.indexOf("yoda : 300") - 1));
                assertTrue(list.contains("yoda : 300"));
                assertEquals(p4, list.get(list.indexOf("luke : 300") - 1));
                assertTrue(list.contains("luke : 300"));
                assertEquals(p5, list.get(list.indexOf("bobba : 300") - 1));
                assertTrue(list.contains("bobba : 300"));
            } else {
                assertEquals(8, list.size());
                assertEquals(p1, list.get(list.indexOf("darth : 100") - 1));
                assertTrue(list.contains("darth : 100"));
                assertEquals(p3, list.get(list.indexOf("yoda : 300") - 1));
                assertTrue(list.contains("yoda : 300"));
                assertEquals(p4, list.get(list.indexOf("luke : 300") - 1));
                assertTrue(list.contains("luke : 300"));
                assertEquals(p5, list.get(list.indexOf("bobba : 300") - 1));
                assertTrue(list.contains("bobba : 300"));
            }
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testQueriesWithNestedAccessorsAllOutputs() throws IOException, ClassNotFoundException {
        String drl = "" +
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( String $name, String $likes, String $street ) \n" +
                "   Person( $name := name, $likes := likes, $street := address.street ) \n" +
                "end\n";

        drl += "rule x1\n" +
                "when\n" +
                "    String( this == \"go1\" )\n" +
                //         output, output,         ,output
                "    ?peeps($name1; $likes1 : $likes, $street1 : $street )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $likes1 + \" : \" +  $street1 );\n" +
                "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p1 = new Person("darth",
                                         "stilton",
                                         100);
            p1.setAddress(new Address("s1"));

            final Person p2 = new Person("yoda",
                                         "stilton",
                                         300);
            p2.setAddress(new Address("s2"));

            ksession.insert(p1);
            ksession.insert(p2);

            ksession.insert("go1");
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            assertEquals(2, list.size());
            assertTrue(list.contains("darth : stilton : s1"));
            assertTrue(list.contains("yoda : stilton : s2"));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testQueriesWithNestedAcecssorsMixedArgs() {
        String drl = "" +
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( String $name, String $likes, String $street ) \n" +
                "   Person( $name := name, $likes := likes, $street := address.street ) \n" +
                "end\n";

        drl += "rule x1\n" +
                "when\n" +
                "    $s : String()\n" +
                //         output, output,         ,input
                "    ?peeps($name1; $likes1 : $likes, $street : $s )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $likes1 + \" : \" +  $s );\n" +
                "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p1 = new Person("darth",
                                         "stilton",
                                         100);
            p1.setAddress(new Address("s1"));

            final Person p2 = new Person("yoda",
                                         "stilton",
                                         300);
            p2.setAddress(new Address("s2"));

            ksession.insert(p1);
            ksession.insert(p2);

            ksession.insert("s1");
            ksession.fireAllRules();
            assertEquals(1, list.size());
            assertTrue(list.contains("darth : stilton : s1"));

            list.clear();
            ksession.insert("s2");
            ksession.fireAllRules();
            assertEquals(1, list.size());
            assertTrue(list.contains("yoda : stilton : s2"));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testQueryWithDynamicData() throws IOException, ClassNotFoundException {
        String drl = "" +
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
                "    $p := Person( ) from new Person( $name, $likes, $age ) \n" +
                "end\n";

        drl += "rule x1\n" +
                "when\n" +
                "    $n1 : String( )\n" +
                //     output, input     ,input                 ,input
                "    ?peeps($p; $name : $n1, $likes : \"stilton\", $age : 100 )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p1 = new Person("darth",
                                         "stilton",
                                         100);

            final Person p2 = new Person("yoda",
                                         "stilton",
                                         100);

            ksession.insert("darth");
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            assertEquals(1, list.size());
            assertEquals(p1, list.get(0));

            list.clear();
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.insert("yoda");
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            assertEquals(1, list.size());
            assertEquals(p2, list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testQueryWithDyanmicInsert() throws IOException, ClassNotFoundException {
        String drl = "" +
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
                "    $p := Person( ) from new Person( $name, $likes, $age ) \n" +
                "end\n";

        drl += "rule x1\n" +
                "when\n" +
                "    $n1 : String( )\n" +
                "    not Person( name == 'darth' )\n " +
                //     output, input     ,input                 ,input
                "    ?peeps($p; $name : $n1, $likes : \"stilton\", $age : 100 )\n" +
                "then\n" +
                "   insert( $p );\n" +
                "end \n";

        drl += "rule x2\n" +
                "when\n" +
                "    $p : Person( )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p1 = new Person("darth",
                                         "stilton",
                                         100);

            ksession.insert("darth");
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            ksession.insert("yoda"); // darth exists, so yoda won't get created
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            assertEquals(1, list.size());
            assertEquals(p1, list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testQueryWithOr() {
        final String drl = "" +
                "package org.drools.compiler.test  \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +
                "import " + BackwardChainingTest.class.getName() + ".Q\n" +
                "import " + BackwardChainingTest.class.getName() + ".R\n" +
                "import " + BackwardChainingTest.class.getName() + ".S\n" +

                "global List list\n" +

                "dialect \"mvel\"\n" +
                "\n" +
                "query q(int x)\n" +
                "    Q( x := value )\n" +
                "end\n" +
                "\n" +
                "query r(int x)\n" +
                "    R( x := value )\n" +
                "end\n" +
                "\n" +
                "query s(int x)\n" +
                "    S( x := value )    \n" +
                "end\n" +
                "\n" +

                "query p(int x)\n" +
                "    (?q(x;) and ?r(x;) ) \n" +
                "    or\n" +
                "    ?s(x;)\n" +
                "end\n" +

                "rule init when\n" +
                "then\n" +
                " insert( new Q(1) );\n " +
                " insert( new Q(5) );\n " +
                " insert( new Q(6) );\n " +
                " insert( new R(1) );\n " +
                " insert( new R(4) );\n " +
                " insert( new R(6) );\n " +
                " insert( new R(2) );\n " +
                " insert( new S(2) );\n " +
                " insert( new S(3) );\n " +
                " insert( new S(6) );\n " +
                "end\n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();

            QueryResults results;

            list.clear();
            results = ksession.getQueryResults("p", new Integer[]{0});
            for (final QueryResultsRow result : results) {
                list.add((Integer) result.get("x"));
            }
            assertEquals(0, list.size());

            list.clear();
            results = ksession.getQueryResults("p", new Integer[]{1});
            for (final QueryResultsRow result : results) {
                list.add((Integer) result.get("x"));
            }

            assertEquals(1, list.size());
            assertEquals(1, list.get(0).intValue());

            list.clear();
            results = ksession.getQueryResults("p", new Integer[]{2});
            for (final QueryResultsRow result : results) {
                list.add((Integer) result.get("x"));
            }
            assertEquals(1, list.size());
            assertEquals(2, list.get(0).intValue());

            list.clear();
            results = ksession.getQueryResults("p", new Integer[]{3});
            for (final QueryResultsRow result : results) {
                list.add((Integer) result.get("x"));
            }
            assertEquals(1, list.size());
            assertEquals(3, list.get(0).intValue());

            list.clear();
            results = ksession.getQueryResults("p", new Integer[]{4});
            for (final QueryResultsRow result : results) {
                list.add((Integer) result.get("x"));
            }
            assertEquals(0, list.size());

            list.clear();
            results = ksession.getQueryResults("p", new Integer[]{5});
            for (final QueryResultsRow result : results) {
                list.add((Integer) result.get("x"));
            }
            assertEquals(0, list.size());

            list.clear();
            results = ksession.getQueryResults("p", new Integer[]{6});
            for (final QueryResultsRow result : results) {
                list.add((Integer) result.get("x"));
            }
            assertEquals(2, list.size());
            assertEquals(6, list.get(0).intValue());
            assertEquals(6, list.get(1).intValue());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testGeneology() throws IOException, ClassNotFoundException {
        // from http://kti.mff.cuni.cz/~bartak/prolog/genealogy.html

        final String drl = "" +
                "package org.drools.compiler.test2  \n" +
                "global java.util.List list\n" +
                "dialect \"mvel\"\n" +

                "query man( String name ) \n" +
                "   " + BackwardChainingTest.class.getName() + ".Man( name := name ) \n" +
                "end\n" +

                "query woman( String name ) \n" +
                "   " + BackwardChainingTest.class.getName() + ".Woman( name := name ) \n" +
                "end\n" +

                "query parent( String parent, String child ) \n" +
                "   " + BackwardChainingTest.class.getName() + ".Parent( parent := parent, child := child ) \n" +
                "end\n" +

                "query father( String father, String child ) \n" +
                "   ?man( father; ) \n" +
                "   ?parent( father, child; ) \n" +
                "end\n" +

                "query mother( String mother, String child ) \n" +
                "   ?woman( mother; ) \n" +
                "   ?parent( mother, child; ) \n" +
                "end\n" +

                "query son( String son, String parent ) \n" +
                "   ?man( son; ) \n" +
                "   ?parent( parent, son; ) \n" +
                "end\n" +

                "query daughter( String daughter, String parent ) \n" +
                "   ?woman( daughter; ) \n" +
                "   ?parent( parent, daughter; ) \n" +
                "end\n" +

                "query siblings( String c1, String c2 ) \n" +
                "   ?parent( $p, c1; ) \n" +
                "   ?parent( $p, c2; ) \n" +
                "   eval( !c1.equals( c2 ) )\n" +
                "end\n" +

                "query fullSiblings( String c1, String c2 )\n" +
                "   ?parent( $p1, c1; ) ?parent( $p1, c2; )\n" +
                "   ?parent( $p2, c1; ) ?parent( $p2, c2; )\n" +
                "   eval( !c1.equals( c2 ) && !$p1.equals( $p2 )  )\n" +
                "end\n" +

                "query fullSiblings2( String c1, String c2 )\n" +
                "   ?father( $p1, c1; ) ?father( $p1, c2; )\n" +
                "   ?mother( $p2, c1; ) ?mother( $p2, c2; )\n" +
                "   eval( !c1.equals( c2 ) )\n" +
                "end\n" +

                "query uncle( String uncle, String n )\n" +
                "   ?man( uncle; ) ?siblings( uncle, parent; )\n" +
                "   ?parent( parent, n; )\n " +
                "end\n" +

                "query aunt( String aunt, String n )\n" +
                "   ?woman( aunt; ) ?siblings( aunt, parent; )\n" +
                "   ?parent( parent, n; )\n " +
                "end\n" +

                "query grantParents( String gp, String gc )\n" +
                "   ?parent( gp, p; ) ?parent( p, gc; )\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            // grand parents
            ksession.insert(new Man("john"));
            ksession.insert(new Woman("janet"));

            // parent
            ksession.insert(new Man("adam"));
            ksession.insert(new Parent("john",
                                       "adam"));
            ksession.insert(new Parent("janet",
                                       "adam"));

            ksession.insert(new Man("stan"));
            ksession.insert(new Parent("john",
                                       "stan"));
            ksession.insert(new Parent("janet",
                                       "stan"));

            // grand parents
            ksession.insert(new Man("carl"));
            ksession.insert(new Woman("tina"));
            //
            // parent
            ksession.insert(new Woman("eve"));
            ksession.insert(new Parent("carl",
                                       "eve"));
            ksession.insert(new Parent("tina",
                                       "eve"));
            //
            // parent
            ksession.insert(new Woman("mary"));
            ksession.insert(new Parent("carl",
                                       "mary"));
            ksession.insert(new Parent("tina",
                                       "mary"));

            ksession.insert(new Man("peter"));
            ksession.insert(new Parent("adam",
                                       "peter"));
            ksession.insert(new Parent("eve",
                                       "peter"));

            ksession.insert(new Man("paul"));
            ksession.insert(new Parent("adam",
                                       "paul"));
            ksession.insert(new Parent("mary",
                                       "paul"));

            ksession.insert(new Woman("jill"));
            ksession.insert(new Parent("adam",
                                       "jill"));
            ksession.insert(new Parent("eve",
                                       "jill"));

            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            list.clear();
            QueryResults results = ksession.getQueryResults("woman", v);
            for (final QueryResultsRow result : results) {
                list.add((String) result.get("name"));
            }
            assertEquals(5, list.size());
            assertContains(new String[]{"janet", "mary", "tina", "eve", "jill"}, list);

            list.clear();
            results = ksession.getQueryResults("man", v);
            for (final QueryResultsRow result : results) {
                list.add((String) result.get("name"));
            }
            assertEquals(6, list.size());
            assertContains(new String[]{"stan", "john", "peter", "carl", "adam", "paul"}, list);

            list.clear();
            results = ksession.getQueryResults("father", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("father") + ", " + result.get("child"));
            }
            assertEquals(7, list.size());
            assertContains(new String[]{"john, adam", "john, stan",
                                   "carl, eve", "carl, mary",
                                   "adam, peter", "adam, paul",
                                   "adam, jill"}, list);

            list.clear();
            results = ksession.getQueryResults("mother", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("mother") + ", " + result.get("child"));
            }
            assertEquals(7, list.size());
            assertContains(new String[]{"janet, adam", "janet, stan",
                                   "mary, paul", "tina, eve",
                                   "tina, mary", "eve, peter",
                                   "eve, jill"},
                           list);

            list.clear();
            results = ksession.getQueryResults("son",
                                               v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("son") + ", " + result.get("parent"));
            }
            assertEquals(8,
                         list.size());
            assertContains(new String[]{"stan, john", "stan, janet",
                                   "peter, adam", "peter, eve",
                                   "adam, john", "adam, janet",
                                   "paul, mary", "paul, adam"}, list);

            list.clear();
            results = ksession.getQueryResults("daughter", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("daughter") + ", " + result.get("parent"));
            }
            assertEquals(6, list.size());
            assertContains(new String[]{"mary, carl", "mary, tina",
                                   "eve, carl", "eve, tina",
                                   "jill, adam", "jill, eve"}, list);

            list.clear();
            results = ksession.getQueryResults("siblings", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("c1") + ", " + result.get("c2"));
            }
            assertEquals(16, list.size());
            assertContains(new String[]{"eve, mary", "mary, eve",
                                   "adam, stan", "stan, adam",
                                   "adam, stan", "stan, adam",
                                   "peter, paul", "peter, jill",
                                   "paul, peter", "paul, jill",
                                   "jill, peter", "jill, paul",
                                   "peter, jill", "jill, peter",
                                   "eve, mary", "mary, eve"}, list);

            list.clear();
            results = ksession.getQueryResults("fullSiblings", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("c1") + ", " + result.get("c2"));
            }
            assertEquals(12, list.size());
            assertContains(new String[]{"eve, mary", "mary, eve",
                                   "adam, stan", "stan, adam",
                                   "adam, stan", "stan, adam",
                                   "peter, jill", "jill, peter",
                                   "peter, jill", "jill, peter",
                                   "eve, mary", "mary, eve"}, list);

            list.clear();
            results = ksession.getQueryResults("fullSiblings", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("c1") + ", " + result.get("c2"));
            }
            assertEquals(12, list.size());
            assertContains(new String[]{"eve, mary", "mary, eve",
                                   "adam, stan", "stan, adam",
                                   "adam, stan", "stan, adam",
                                   "peter, jill", "jill, peter",
                                   "peter, jill", "jill, peter",
                                   "eve, mary", "mary, eve"}, list);

            list.clear();
            results = ksession.getQueryResults("uncle", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("uncle") + ", " + result.get("n"));
            }
            assertEquals(6, list.size());
            assertContains(new String[]{"stan, peter",
                                   "stan, paul",
                                   "stan, jill",
                                   "stan, peter",
                                   "stan, paul",
                                   "stan, jill"}, list);

            list.clear();
            results = ksession.getQueryResults("aunt", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("aunt") + ", " + result.get("n"));
            }
            assertEquals(6, list.size());
            assertContains(new String[]{"mary, peter",
                                   "mary, jill",
                                   "mary, peter",
                                   "mary, jill",
                                   "eve, paul",
                                   "eve, paul"}, list);

            list.clear();
            results = ksession.getQueryResults("grantParents", v, v);
            for (final QueryResultsRow result : results) {
                list.add(result.get("gp") + ", " + result.get("gc"));
            }
            assertEquals(12, list.size());
            assertContains(new String[]{"carl, peter",
                                   "carl, jill",
                                   "carl, paul",
                                   "john, peter",
                                   "john, paul",
                                   "john, jill",
                                   "janet, peter",
                                   "janet, paul",
                                   "janet, jill",
                                   "tina, peter",
                                   "tina, jill",
                                   "tina, paul",}, list);
        } finally {
            ksession.dispose();
        }
    }

    @Test()
    public void testDynamicRulesWithSharing() {
        String drl = "" +
                "package org.drools.compiler.test1  \n" +
                "\n" +
                "declare Location\n" +
                "    thing : String \n" +
                "    location : String \n" +
                "end" +
                "\n" +
                "declare Edible\n" +
                "   thing : String\n" +
                "end" +
                "\n" +
                "query whereFood( String x, String y ) \n" +
                "    Location(x, y;) Edible(x;) \n" +
                "end\n" +
                "\n" +
                "rule init when\n" +
                "then\n" +
                "        \n" +
                "        insert( new Location(\"apple\", \"kitchen\") );\n" +
                "        insert( new Location(\"crackers\", \"kitchen\") );\n" +
                "        insert( new Location(\"broccoli\", \"kitchen\") );\n" +
                "        insert( new Location(\"computer\", \"office\") );\n" +

                "        insert( new Edible(\"apple\") );\n" +
                "        insert( new Edible(\"crackers\") );\n" +
                "end\n" +
                "";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration);
        final KnowledgeBuilder knowledgeBuilder = TestUtil.createKnowledgeBuilder(kieBase, drl);

        drl = "" +
                "package org.drools.compiler.test2  \n" +

                "import org.drools.compiler.test1.*\n" +
                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "import java.util.Map\n" +
                "import java.util.HashMap\n" +

                "global List list\n" +
                "\n" +
                "rule look2 when\n" +
                "     $place : String() // just here to give a OTN lookup point\n" +
                "     whereFood(thing, $place;)\n" +
                "then\n" +
                "      list.add( \"2:\" + thing );\n" +
                "end\n";

        knowledgeBuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);

        if (knowledgeBuilder.hasErrors()) {
            fail(knowledgeBuilder.getErrors().toString());
        }

        drl = "" +
                "package org.drools.compiler.test3  \n" +

                "import org.drools.compiler.test1.*\n" +
                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "import java.util.Map\n" +
                "import java.util.HashMap\n" +

                "global List list\n" +
                "\n" +
                "rule look3 when\n" +
                "     $place : String() // just here to give a OTN lookup point\n" +
                "     whereFood(thing, $place;)\n" +
                "then\n" +
                "      list.add( \"3:\" + thing );\n" +
                "end\n";

        knowledgeBuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);

        if (knowledgeBuilder.hasErrors()) {
            fail(knowledgeBuilder.getErrors().toString());
        }

        drl = "" +
                "package org.drools.compiler.test4  \n" +

                "import org.drools.compiler.test1.*\n" +
                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "import java.util.Map\n" +
                "import java.util.HashMap\n" +

                "global List list\n" +
                "\n" +
                "rule look4 when\n" +
                "     $place : String() // just here to give a OTN lookup point\n" +
                "     whereFood(thing, $place;)\n" +
                "then\n" +
                "      list.add( \"4:\" + thing );\n" +
                "end\n";

        knowledgeBuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);

        if (knowledgeBuilder.hasErrors()) {
            fail(knowledgeBuilder.getErrors().toString());
        }

        final Map<String, KiePackage> pkgs = new HashMap<>();
        for (final KiePackage pkg : knowledgeBuilder.getKnowledgePackages()) {
            pkgs.put(pkg.getName(), pkg);
        }

        final InternalKnowledgeBase kbase =
                (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration);
        kbase.addPackages(Arrays.asList(pkgs.get("org.drools.compiler.test1"), pkgs.get("org.drools.compiler.test2")));

        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Map<String, Object>> list = new ArrayList<>();
            ksession.setGlobal("list", list);
            ksession.insert("kitchen");

            ksession.fireAllRules();

            assertEquals(2, list.size());
            assertContains(new String[]{"2:crackers", "2:apple"}, list);

            list.clear();
            kbase.addPackages(Collections.singletonList(pkgs.get("org.drools.compiler.test3")));

            ksession.fireAllRules();
            assertEquals(2, list.size());
            assertContains(new String[]{"3:crackers", "3:apple"}, list);

            list.clear();
            kbase.addPackages(Collections.singletonList(pkgs.get("org.drools.compiler.test4")));

            ksession.fireAllRules();
            assertEquals(2, list.size());
            assertContains(new String[]{"4:crackers", "4:apple"}, list);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOpenBackwardChain() {
        // http://www.amzi.com/AdventureInProlog/advtop.php

        final String drl = "" +
                "package org.drools.compiler.test  \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +
                "import " + Person.class.getCanonicalName() + "\n" +

                "global List list\n" +

                "dialect \"mvel\"\n" +

                "declare Location\n" +
                "    thing : String \n" +
                "    location : String \n" +
                "end" +
                "\n" +
                "query isContainedIn( String x, String y ) \n" +
                "    Location(x, y;)\n" +
                "    or \n" +
                "    ( Location(z, y;) and isContainedIn(x, z;) )\n" +
                "end\n" +
                "\n" +
                "rule look when \n" +
                "    Person( $l : likes ) \n" +
                "    isContainedIn( $l, 'office'; )\n" +
                "then\n" +
                "   insertLogical( 'blah' );" +
                "end\n" +
                "rule existsBlah when \n" +
                "    exists String( this == 'blah') \n" +
                "then\n" +
                "   list.add( 'exists blah' );" +
                "end\n" +
                "\n" +
                "rule notBlah when \n" +
                "    not String( this == 'blah') \n" +
                "then\n" +
                "   list.add( 'not blah' );" +
                "end\n" +
                "\n" +
                "rule init when\n" +
                "then\n" +
                "        insert( new Location(\"desk\", \"office\") );\n" +
                "        insert( new Location(\"envelope\", \"desk\") );\n" +
                "        insert( new Location(\"key\", \"envelope\") );\n" +
                "end\n" +
                "\n" +
                "rule go1 when \n" +
                "    String( this == 'go1') \n" +
                "then\n" +
                "        list.add( drools.getRule().getName() ); \n" +
                "        insert( new Location('lamp', 'desk') );\n" +
                "end\n" +
                "\n" +
                "rule go2 when \n" +
                "    String( this == 'go2') \n" +
                "    $l : Location('lamp', 'desk'; )\n" +
                "then\n" +
                "    list.add( drools.getRule().getName() ); \n" +
                "    retract( $l );\n" +
                "end\n" +
                "\n" +
                "rule go3 when \n" +
                "    String( this == 'go3') \n" +
                "then\n" +
                "        list.add( drools.getRule().getName() ); \n" +
                "        insert( new Location('lamp', 'desk') );\n" +
                "end\n" +
                "\n" +
                "rule go4 when \n" +
                "    String( this == 'go4') \n" +
                "    $l : Location('lamp', 'desk'; )\n" +
                "then\n" +
                "        list.add( drools.getRule().getName() ); \n" +
                "    modify( $l ) { thing = 'book' };\n" +
                "end\n" +
                "\n" +
                "rule go5 when \n" +
                "    String( this == 'go5') \n" +
                "    $l : Location('book', 'desk'; )\n" +
                "then\n" +
                "    list.add( drools.getRule().getName() ); \n" +
                "    modify( $l ) { thing = 'lamp' };\n" +
                "end\n" +
                "\n" +
                "rule go6 when \n" +
                "    String( this == 'go6') \n" +
                "    $l : Location( 'lamp', 'desk'; )\n" +
                "then\n" +
                "    list.add( drools.getRule().getName() ); \n" +
                "    modify( $l ) { thing = 'book' };\n" +
                "end\n" +
                "\n" +
                "rule go7 when \n" +
                "    String( this == 'go7') \n" +
                "    $p : Person( likes == 'lamp' ) \n" +
                "then\n" +
                "    list.add( drools.getRule().getName() ); \n" +
                "    modify( $p ) { likes = 'key' };\n" +
                "end\n" +
                "\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p = new Person();
            p.setLikes("lamp");
            ksession.insert(p);
            ksession.fireAllRules();
            assertEquals("not blah", list.get(0));

            list.clear();

            InternalFactHandle fh = (InternalFactHandle) ksession.insert("go1");
            ksession.fireAllRules();

            fh = getFactHandle(fh, ksession);
            ksession.delete(fh);
            assertEquals("go1", list.get(0));
            assertEquals("exists blah", list.get(1));

            fh = (InternalFactHandle) ksession.insert("go2");
            ksession.fireAllRules();

            fh = getFactHandle(fh, ksession);
            ksession.delete(fh);
            assertEquals("go2", list.get(2));
            assertEquals("not blah", list.get(3));

            fh = (InternalFactHandle) ksession.insert("go3");
            ksession.fireAllRules();

            fh = getFactHandle(fh, ksession);
            ksession.delete(fh);
            assertEquals("go3", list.get(4));
            assertEquals("exists blah", list.get(5));

            fh = (InternalFactHandle) ksession.insert("go4");
            ksession.fireAllRules();

            fh = getFactHandle(fh, ksession);
            ksession.delete(fh);
            assertEquals("go4", list.get(6));
            assertEquals("not blah", list.get(7));

            fh = (InternalFactHandle) ksession.insert("go5");
            ksession.fireAllRules();

            fh = getFactHandle(fh, ksession);
            ksession.delete(fh);
            assertEquals("go5", list.get(8));
            assertEquals("exists blah", list.get(9));

            // This simulates a modify of the root DroolsQuery object, but first we break it
            fh = (InternalFactHandle) ksession.insert("go6");
            ksession.fireAllRules();

            fh = getFactHandle(fh, ksession);
            ksession.delete(fh);
            assertEquals("go6", list.get(10));
            assertEquals("not blah", list.get(11));

            // now fix it
            fh = (InternalFactHandle) ksession.insert("go7");
            ksession.fireAllRules();

            fh = getFactHandle(fh, ksession);
            ksession.delete(fh);
            assertEquals("go7", list.get(12));
            assertEquals("exists blah", list.get(13));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testCompile() {
        final String drl = "declare Location\n"
                + "thing : String\n"
                + "location : String\n"
                + "end\n\n"
                + "query isContainedIn( String x, String y )\n"
                + "Location( x := thing, y := location)\n"
                + "or \n"
                + "( Location(z := thing, y := location) and ?isContainedIn( x := x, z := y ) )\n"
                + "end\n";

        KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
    }

    @Test(timeout = 10000)
    public void testInsertionOrder() {
        final String drl = "" +
                "package org.drools.compiler.integrationtests  \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "global List list\n" +

                "dialect \"mvel\"\n" +

                "declare Person\n" +
                "   name : String\n" +
                "   likes : String\n" +
                "end\n" +
                "\n" +
                "declare Location\n" +
                "    thing : String \n" +
                "    location : String \n" +
                "end\n" +
                "\n" +
                "declare Edible\n" +
                "   thing : String\n" +
                "end\n" +
                "\n" +
                "\n" +
                "query hasFood( String x, String y ) \n" +
                "    Location(x, y;) " +
                "     or \n " +
                "    ( Location(z, y;) and hasFood(x, z;) )\n" +
                "end\n" +
                "\n" +
                "rule look when \n" +
                "    Person( $l : likes ) \n" +
                "    hasFood( $l, 'kitchen'; )\n" +
                "then\n" +
                "   list.add( 'kitchen has ' + $l );" +
                "end\n" +
                "rule go1 when\n" +
                "    String( this == 'go1') \n" +
                "then\n" +
                "        insert( new Person('zool', 'peach') );\n" +
                "        insert( new Location(\"table\", \"kitchen\") );\n" +
                "        insert( new Location(\"peach\", \"table\") );\n" +
                "end\n" +
                "rule go2 when\n" +
                "    String( this == 'go2') \n" +
                "then\n" +
                "        insert( new Person('zool', 'peach') );\n" +
                "        insert( new Location(\"peach\", \"table\") );\n" +
                "        insert( new Location(\"table\", \"kitchen\") );\n" +
                "end\n" +
                "\n" +
                "rule go3 when\n" +
                "    String( this == 'go3') \n" +
                "then\n" +
                "        insert( new Location(\"table\", \"kitchen\") );\n" +
                "        insert( new Location(\"peach\", \"table\") );\n" +
                "        insert( new Person('zool', 'peach') );\n" +
                "end\n" +
                "\n" +
                "rule go4 when\n" +
                "    String( this == 'go4') \n" +
                "then\n" +
                "        insert( new Location(\"peach\", \"table\") );\n" +
                "        insert( new Location(\"table\", \"kitchen\") );\n" +
                "        insert( new Person('zool', 'peach') );\n" +
                "end\n" +
                "rule go5 when\n" +
                "    String( this == 'go5') \n" +
                "then\n" +
                "        insert( new Location(\"peach\", \"table\") );\n" +
                "        insert( new Person('zool', 'peach') );\n" +
                "        insert( new Location(\"table\", \"kitchen\") );\n" +
                "end\n" +
                "rule go6 when\n" +
                "    String( this == 'go6') \n" +
                "then\n" +
                "        insert( new Location(\"table\", \"kitchen\") );\n" +
                "        insert( new Person('zool', 'peach') );\n" +
                "        insert( new Location(\"peach\", \"table\") );\n" +
                "end\n" +
                "\n" +
                "\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);

        for (int i = 1; i <= 6; i++) {
            final KieSession ksession = kbase.newKieSession();
            try {
                final List<String> list = new ArrayList<>();
                ksession.setGlobal("list", list);
                ksession.fireAllRules();
                list.clear();
                final FactHandle fh = ksession.insert("go" + i);
                ksession.fireAllRules();
                ksession.delete(fh);
                assertEquals(1, list.size());
                assertEquals("kitchen has peach", list.get(0));
            } finally {
                ksession.dispose();
            }
        }
    }

    @Test(timeout = 10000)
    public void testQueryFindAll() {
        final Object[] objects = new Object[]{42, "a String", 100};
        final int oCount = objects.length;

        final List<Object> queryList = new ArrayList<>();
        final List<Object> ruleList = new ArrayList<>();
        // expect all inserted objects + InitialFact
        runTestQueryFindAll(0, queryList, ruleList, objects);

        assertEquals(oCount, queryList.size());
        assertContains(objects, queryList);

        // expect inserted objects + InitialFact
        queryList.clear();
        ruleList.clear();
        runTestQueryFindAll(1, queryList, ruleList, objects);
        assertEquals(oCount * oCount, queryList.size());

        queryList.clear();
        ruleList.clear();
        runTestQueryFindAll(2, queryList, ruleList, objects);
        assertEquals(oCount * oCount, queryList.size());
    }

    private void runTestQueryFindAll(final int iCase,
                                     final List<Object> queryList,
                                     final List<Object> ruleList,
                                     final Object[] objects) {
        String drl = "" +
                "package org.drools.compiler.test \n" +
                "global java.util.List queryList \n" +
                "global java.util.List ruleList \n" +
                "query object( Object o ) \n" +
                "    o := Object( ) \n" +
                "end \n" +
                "rule findObjectByQuery \n" +
                "when \n";
        switch (iCase) {
            case 0:
                // omit Object()
                drl += "    object( $a ; ) \n";
                break;
            case 1:
                drl += "    Object() ";
                drl += "    object( $a ; ) \n";
                break;
            case 2:
                drl += "    object( $a ; ) \n";
                drl += "    Object() ";
                break;
        }
        drl +=
                "then \n" +
                "//   System.out.println( \"Object by query: \" + $a );\n" +
                "    queryList.add( $a ); \n" +
                "end \n" +
                "rule findObject \n" +
                "salience 10 \n" +
                "when \n" +
                "    $o: Object() \n" +
                "then " +
                "//   System.out.println( \"Object: \" + $o );\n" +
                "    ruleList.add( $o ); \n" +
                "end \n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("queryList", queryList);
            ksession.setGlobal("ruleList", ruleList);
            for (final Object o : objects) {
                ksession.insert(o);
            }
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testQueryWithObject() {
        final String drl = "" +
                "package org.drools.compiler.test  \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "global List list\n" +

                "dialect \"mvel\"\n" +
                "\n" +

                "import " + BackwardChainingTest.class.getName() + ".Q\n" +
                "import " + BackwardChainingTest.class.getName() + ".R\n" +
                "import " + BackwardChainingTest.class.getName() + ".S\n" +

                "query object(Object o)\n" +
                "    o := Object() \n" +
                "end\n" +

                "rule collectObjects when\n" +
                "   String( this == 'go1' )\n" +
                "   object( o; )\n" +
                "then\n" +
                "   list.add( o );\n" +
                "end\n" +

                "rule init when\n" +
                "   String( this == 'init' )\n" +
                "then\n" +
                " insert( new Q(1) );\n " +
                " insert( new Q(5) );\n " +
                " insert( new Q(6) );\n " +
                " insert( new R(1) );\n " +
                " insert( new R(4) );\n " +
                " insert( new R(6) );\n " +
                " insert( new R(2) );\n " +
                " insert( new S(2) );\n " +
                " insert( new S(3) );\n " +
                " insert( new S(6) );\n " +
                "end\n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        List<Integer> list = new ArrayList<>();
        try {
            ksession.setGlobal("list", list);

            ksession.insert("init");
            ksession.fireAllRules();

            ksession.insert("go1");
            ksession.fireAllRules();

            assertEquals(12, list.size());
            assertContains(new Object[]{
                    "go1", "init", new Q(6), new R(6), new S(3),
                    new R(2), new R(1), new R(4), new S(2),
                    new S(6), new Q(1), new Q(5)},
                           list);
        } finally {
            ksession.dispose();
        }

        // now reverse the go1 and init order
        ksession = kbase.newKieSession();
        try {
            list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert("go1");
            ksession.fireAllRules();

            ksession.insert("init");
            ksession.fireAllRules();

            assertEquals(12, list.size());
            assertContains(new Object[]{
                    "go1", "init", new Q(6), new R(6), new S(3),
                    new R(2), new R(1), new R(4), new S(2),
                    new S(6), new Q(1), new Q(5)},
                           list);
        } finally {
            ksession.dispose();
        }
    }

    public static void assertContains(final Object[] objects,
                                final List list) {
        for (final Object object : objects) {
            if (!list.contains(object)) {
                fail("does not contain:" + object);
            }
        }
    }

    public static void assertContains(final List objects,
                                final List list) {
        if (!list.contains(objects)) {
            fail("does not contain:" + objects);
        }
    }

    public static InternalFactHandle getFactHandle(final FactHandle factHandle,
                                            final KieSession ksession) {
        final Map<Long, FactHandle> handles = new HashMap<>();
        ksession.getFactHandles().forEach(fh -> handles.put(((InternalFactHandle) fh).getId(), fh));
        return (InternalFactHandle) handles.get(((InternalFactHandle) factHandle).getId());
    }

    public static class Man
            implements
            Serializable {

        private String name;

        public Man(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Man [name=" + name + "]";
        }
    }

    public static class Woman
            implements
            Serializable {

        private String name;

        public Woman(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Woman [name=" + name + "]";
        }
    }

    public static class Parent
            implements
            Serializable {

        private String parent;
        private String child;

        public Parent(final String parent,
                      final String child) {
            this.parent = parent;
            this.child = child;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(final String parent) {
            this.parent = parent;
        }

        public String getChild() {
            return child;
        }

        public void setChild(final String child) {
            this.child = child;
        }

        @Override
        public String toString() {
            return "Parent [parent=" + parent + ", child=" + child + "]";
        }
    }

    public static class Q {

        int value;

        public Q(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(final int value) {
            this.value = value;
        }

        public String toString() {
            return "Q" + value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + value;
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Q other = (Q) obj;
            return value == other.value;
        }
    }

    public static class R {

        int value;

        public R(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(final int value) {
            this.value = value;
        }

        public String toString() {
            return "R" + value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + value;
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final R other = (R) obj;
            return value == other.value;
        }
    }

    public static class S {

        int value;

        public S(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(final int value) {
            this.value = value;
        }

        public String toString() {
            return "S" + value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + value;
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final S other = (S) obj;
            return value == other.value;
        }
    }

    @Test(timeout = 10000)
    public void testQueryWithClassLiterals() {
        final String drl = "" +
                "package org.drools.test \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "global List list\n" +

                "declare Foo end \n" +

                "query klass( Class $c )\n" +
                " Object( this.getClass() == $c ) \n" +
                "end\n" +

                "rule R when\n" +
                " o : String( this == 'go1' )\n" +
                " klass( String.class ; )\n" +
                "then\n" +
                " list.add( o );\n" +
                " insert( new Foo() ); \n" +
                "end\n" +

                "rule S when\n" +
                " o : Foo()\n" +
                " klass( Foo.class ; )\n" +
                "then\n" +
                " list.add( o );\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.insert("go1");
            ksession.fireAllRules();

            assertEquals(2, list.size());
            assertEquals("go1", list.get(0));
            assertEquals("org.drools.test.Foo", list.get(1).getClass().getName());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testQueryIndexingWithUnification() {
        final String drl = "" +
                "package org.drools.test \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "global List list\n" +

                "declare Foo id : int end \n" +
                "declare Bar " +
                " name : String " +
                " val : int " +
                "end \n" +

                "query fooffa( String $name, Foo $f )\n" +
                " Bar( name == $name, $id : val )\n" +
                " $f := Foo( id == $id ) \n" +
                "end\n" +

                "rule R when\n" +
                " o : String( this == 'go' )\n" +
                " fooffa( \"x\", $f ; )\n" +
                "then\n" +
                " list.add( $f );\n" +
                "end\n" +

                "rule S when\n" +
                "then\n" +
                " insert( new Foo( 1 ) );\n" +
                " insert( new Bar( \"x\", 1 ) );\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            ksession.setGlobal("list",
                               list);

            ksession.fireAllRules();

            ksession.insert("go");
            ksession.fireAllRules();

            assertEquals(1, list.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testQueryWithEvents() {
        final String drl = "global java.util.List list; " +
                "" +
                "declare Inner\n" +
                "  @role(event)\n" +
                "end\n" +

                "rule \"Input\"\n" +
                "when\n" +
                "then\n" +
                "    insert( \"X\" );\n" +
                "    insert( new Inner( ) );\n" +
                "end\n" +
                "\n" +
                "query myAgg(  )\n" +
                "    Inner(  )\n" +
                "end\n" +
                "\n" +
                "rule \"React\"\n" +
                "when\n" +
                "    String()\n" +
                "    myAgg(  )\n" +
                "then\n" +
                "    list.add( 42 );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final ArrayList list = new ArrayList();
            ksession.setGlobal("list", list);
            ksession.fireAllRules();
            assertEquals(Collections.singletonList(42), list);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNpeOnQuery() {
        final String drl =
                "global java.util.List list; " +
                        "query foo( Integer $i ) " +
                        "   $i := Integer( this < 10 ) " +
                        "end\n" +
                        "\n" +

                        "rule r1 when " +
                        "   foo( $i ; ) " +
                        "   Integer( this == 10 ) " +
                        "then " +
                        "   System.out.println(\"10 \" + $i);" +
                        "   list.add( 10 );\n" +
                        "end\n" +
                        "\n" +

                        "rule r2 when " +
                        "   foo( $i; ) " +
                        "   Integer( this == 20 ) " +
                        "then " +
                        "   System.out.println(\"20 \" + $i);" +
                        "   list.add( 20 );\n" +
                        "end\n" +

                        "rule r3 when " +
                        "   $i : Integer( this == 1 ) " +
                        "then " +
                        "   System.out.println($i);" +
                        "   update( kcontext.getKieRuntime().getFactHandle( $i ), $i + 1 );" +
                        "end\n" +
                        "\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("backward-chaining-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            kieSession.insert(1);
            kieSession.insert(20);

            kieSession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals(20, (int) list.get(0));
        } finally {
            kieSession.dispose();
        }
    }
}
