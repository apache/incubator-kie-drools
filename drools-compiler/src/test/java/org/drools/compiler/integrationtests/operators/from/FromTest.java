/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.operators.from;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.compiler.Address;
import org.drools.compiler.Cheese;
import org.drools.compiler.Cheesery;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.DomainObjectHolder;
import org.drools.compiler.FromTestClass;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.Person;
import org.drools.compiler.Pet;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialectConfiguration;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class FromTest extends CommonTestMethodBase {

    @Test
    public void testBasicFrom() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_From.drl");
        kbase = SerializationHelper.serializeObject(kbase);

        KieSession ksession = createKnowledgeSession(kbase);
        final List list1 = new ArrayList();
        ksession.setGlobal("list1", list1);
        final List list2 = new ArrayList();
        ksession.setGlobal("list2", list2);
        final List list3 = new ArrayList();
        ksession.setGlobal("list3", list3);

        final Cheesery cheesery = new Cheesery();
        final Cheese stilton = new Cheese("stilton", 12);
        final Cheese cheddar = new Cheese("cheddar", 15);
        cheesery.addCheese(stilton);
        cheesery.addCheese(cheddar);
        ksession.setGlobal("cheesery", cheesery);
        ksession.insert(cheesery);

        final Person p = new Person("stilton");
        ksession.insert(p);

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        // from using a global
        assertEquals(2, ((List) ksession.getGlobal("list1")).size());
        assertEquals(cheddar, ((List) ksession.getGlobal("list1")).get(0));
        assertEquals(stilton, ((List) ksession.getGlobal("list1")).get(1));

        // from using a declaration
        assertEquals(2, ((List) ksession.getGlobal("list2")).size());
        assertEquals(cheddar, ((List) ksession.getGlobal("list2")).get(0));
        assertEquals(stilton, ((List) ksession.getGlobal("list2")).get(1));

        // from using a declaration
        assertEquals(1, ((List) ksession.getGlobal("list3")).size());
        assertEquals(stilton, ((List) ksession.getGlobal("list3")).get(0));
    }

    @Test
    public void testFromWithParams() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_FromWithParams.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        final Object globalObject = new Object();
        ksession.setGlobal("list", list);
        ksession.setGlobal("testObject", new FromTestClass());
        ksession.setGlobal("globalObject", globalObject);

        final Person bob = new Person("bob");
        ksession.insert(bob);

        ksession.fireAllRules();

        assertEquals(6, ((List) ksession.getGlobal("list")).size());

        final List array = (List) ((List) ksession.getGlobal("list")).get(0);
        assertEquals(3, array.size());
        final Person p = (Person) array.get(0);
        assertEquals(p, bob);

        assertEquals(42, array.get(1));

        final List nested = (List) array.get(2);
        assertEquals("x", nested.get(0));
        assertEquals("y", nested.get(1));

        final Map map = (Map) ((List) ksession.getGlobal("list")).get(1);
        assertEquals(2, map.keySet().size());

        assertTrue(map.keySet().contains(bob));
        assertEquals(globalObject, map.get(bob));

        assertTrue(map.keySet().contains("key1"));
        final Map nestedMap = (Map) map.get("key1");
        assertEquals(1, nestedMap.keySet().size());
        assertTrue(nestedMap.keySet().contains("key2"));
        assertEquals("value2", nestedMap.get("key2"));

        assertEquals(42, ((List) ksession.getGlobal("list")).get(2));
        assertEquals("literal", ((List) ksession.getGlobal("list")).get(3));
        assertEquals(bob, ((List) ksession.getGlobal("list")).get(4));
        assertEquals(globalObject, ((List) ksession.getGlobal("list")).get(5));
    }

    @Test
    public void testFromWithNewConstructor() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final PackageDescr descr = parser.parse(new InputStreamReader(getClass().getResourceAsStream("test_FromWithNewConstructor.drl")));

        final Collection<KnowledgePackage> pkgs = loadKnowledgePackages(descr);
        SerializationHelper.serializeObject(pkgs);
    }

    /**
     * JBRULES-1415 Certain uses of from causes NullPointerException in WorkingMemoryLogger
     */
    @Test
    public void testFromDeclarationWithWorkingMemoryLogger() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "import org.drools.compiler.Cheesery\n";
        rule += "import org.drools.compiler.Cheese\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    $cheesery : Cheesery()\n";
        rule += "    Cheese( $type : type) from $cheesery.cheeses\n";
        rule += "then\n";
        rule += "    list.add( $type );\n";
        rule += "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession session = kbase.newKieSession();

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final Cheesery cheesery = new Cheesery();
        cheesery.addCheese(new Cheese("stilton", 22));

        session.insert(cheesery);

        session.fireAllRules();

        assertEquals(1, ((List) session.getGlobal("list")).size());
        assertEquals("stilton", ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testFromArrayIteration() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_FromArrayIteration.drl"));
        final KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();

        session.setGlobal("list", list);
        session.insert(new DomainObjectHolder());

        session.fireAllRules();

        assertEquals(3, list.size());

        assertEquals("Message3", list.get(0));
        assertEquals("Message2", list.get(1));
        assertEquals("Message1", list.get(2));
    }

    @Test
    public void testFromExprFollowedByNot() {
        String rule = "";
        rule += "package org.drools.compiler;\n";
        rule += "global java.util.List list;\n";
        rule += "rule \"Rule 1\"\n";
        rule += "    when\n";
        rule += "        p : Person ($var: pet )\n";
        rule += "        Pet () from $var\n";
        rule += "        not Pet ()\n";
        rule += "    then\n";
        rule += "       list.add( p );\n";
        rule += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person();
        p.setPet(new Pet());
        ksession.insert(p);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertSame(p, list.get(0));
    }

    @Test
    public void testFromNestedAccessors() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_FromNestedAccessors.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Order order1 = new Order(11, "Bob");
        final OrderItem item11 = new OrderItem(order1, 1);
        final OrderItem item12 = new OrderItem(order1, 2);
        order1.addItem(item11);
        order1.addItem(item12);

        ksession.insert(order1);
        ksession.insert(item11);
        ksession.insert(item12);

        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertSame(order1.getStatus(), list.get(0));
    }

    @Test
    public void testFromNodeWithMultipleBetas() throws Exception {
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   $p : Person( $name : name, $addresses : addresses )\n" +
                "   $c : Cheese( $type: type == $name )\n" +
                "   $a : Address( street == $type, suburb == $name ) from $addresses\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final Person p = new Person("x");
        p.addAddress(new Address("x", "x", "x"));
        p.addAddress(new Address("y", "y", "y"));
        ksession.insert(p);

        ksession.insert(new Cheese("x"));
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testFromWithStrictModeOff() {
        // JBRULES-3533
        final String str =
                "import java.util.Map;\n" +
                        "dialect \"mvel\"\n" +
                        "rule \"LowerCaseFrom\"\n" +
                        "when\n" +
                        "   Map($valOne : this['keyOne'] !=null)\n" +
                        "   $lowerValue : String() from $valOne.toLowerCase()\n" +
                        "then\n" +
                        "   System.out.println( $valOne.toLowerCase() );\n" +
                        "end\n";

        final KnowledgeBuilderConfigurationImpl pkgBuilderCfg = new KnowledgeBuilderConfigurationImpl();
        final MVELDialectConfiguration mvelConf = (MVELDialectConfiguration) pkgBuilderCfg.getDialectConfiguration( "mvel" );
        mvelConf.setStrict( false );
        mvelConf.setLangLevel( 5 );

        final KnowledgeBase kbase = loadKnowledgeBaseFromString( pkgBuilderCfg, str );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final Map<String, String> testMap = new HashMap<String, String>();
        testMap.put( "keyOne", "valone" );
        testMap.put( "valTwo", "valTwo" );
        ksession.insert( testMap );
        assertEquals( 1, ksession.fireAllRules() );
        ksession.dispose();
    }

    @Test
    public void testFromWithStrictModeOn() {
        // JBRULES-3533
        final String str =
                "import java.util.Map;\n" +
                        "dialect \"mvel\"\n" +
                        "rule \"LowerCaseFrom\"\n" +
                        "when\n" +
                        "   Map($valOne : this['keyOne'] !=null)\n" +
                        "   $lowerValue : String() from $valOne.toLowerCase()\n" +
                        "then\n" +
                        "   System.out.println( $valOne.toLowerCase() );\n" +
                        "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        assertTrue( kbuilder.hasErrors() );
    }

    @Test
    public void testJavaImplicitWithFrom() throws IOException, ClassNotFoundException {
        testDialectWithFrom("java");
    }

    @Test
    public void testMVELImplicitWithFrom() throws IOException, ClassNotFoundException {
        testDialectWithFrom("mvel");
    }

    private void testDialectWithFrom(final String dialect) throws IOException, ClassNotFoundException {
        final String str = "" +
                "package org.drools.compiler.test \n" +
                "import java.util.List \n" +
                "global java.util.List list \n" +
                "global java.util.List list2 \n" +
                "rule \"show\" dialect \"" + dialect + "\" \n" +
                "when  \n" +
                "    $m : List( eval( size == 0 ) ) from [list] \n" +
                "then \n" +
                "    list2.add('r1'); \n" +
                "end \n";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.setGlobal("list2", list);

        ksession.fireAllRules();
        assertEquals("r1", list.get(0));
    }

    @Test
    public void testMultipleFroms() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_multipleFroms.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Cheesery cheesery = new Cheesery();
        cheesery.addCheese(new Cheese("stilton", 15));
        cheesery.addCheese(new Cheese("brie", 10));

        ksession.setGlobal("cheesery", cheesery);

        ksession.fireAllRules();

        assertEquals(2, results.size());
        assertEquals(2, ((List) results.get(0)).size());
        assertEquals(2, ((List) results.get(1)).size());
    }

    @Test
    public void testNetworkBuildErrorAcrossEntryPointsAndFroms() throws Exception {
        String rule1 = "package org.drools.compiler\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule rule1\n";
        rule1 += "when\n";
        rule1 += "         Cheese() from entry-point \"testep\"\n";
        rule1 += "    $p : Person() from list\n";
        rule1 += "then \n";
        rule1 += "  list.add( \"rule1\" ) ;\n";
        rule1 += "  insert( $p );\n";
        rule1 += "end\n";
        rule1 += "rule rule2\n";
        rule1 += "when\n";
        rule1 += "  $p : Person() \n";
        rule1 += "then \n";
        rule1 += "  list.add( \"rule2\" ) ;\n";
        rule1 += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(rule1);
        final KieSession ksession = createKnowledgeSession(kbase);
        final EntryPoint ep = ksession.getEntryPoint("testep");

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        list.add(new Person("darth"));
        ep.insert(new Cheese("cheddar"));

        ksession.fireAllRules();
        assertEquals(3, list.size());
    }
}
