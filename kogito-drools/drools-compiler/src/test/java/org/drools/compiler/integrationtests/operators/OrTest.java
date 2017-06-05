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

package org.drools.compiler.integrationtests.operators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.common.InternalFactHandle;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class OrTest extends CommonTestMethodBase {

    @Test
    public void testOr() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("or_test.drl"));
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final Cheese cheddar = new Cheese("cheddar", 5);
        final FactHandle h = session.insert(cheddar);

        session.fireAllRules();

        // just one added
        assertEquals("got cheese", list.get(0));
        assertEquals(1, list.size());

        session.delete(h);
        session.fireAllRules();

        // still just one
        assertEquals(1, list.size());

        session.insert(new Cheese("stilton", 5));
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();

        // now have one more
        assertEquals(2, ((List) session.getGlobal("list")).size());
    }

    @Test
    public void testOrCE() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_OrCE.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        ksession.insert(new Cheese("brie", 10));
        ksession.insert(new Person("bob"));

        ksession.fireAllRules();

        assertEquals("should have fired once", 1, list.size());
    }

    @Test
    public void testOrCEFollowedByEval() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_OrCEFollowedByEval.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        ksession.insert(new FactA("X"));
        final InternalFactHandle b = (InternalFactHandle) ksession.insert(new FactB("X"));

        ksession.fireAllRules();

        assertEquals("should have fired", 2, list.size());
        assertTrue(list.contains(b.getObject()));
    }

    @Test
    public void testOrWithAndUsingNestedBindings() throws IOException, ClassNotFoundException {
        String str = "";
        str += "package org.drools.compiler\n";
        str += "import org.drools.compiler.Person\n";
        str += "global java.util.List mlist\n";
        str += "global java.util.List jlist\n";
        str += "rule rule1 dialect \"mvel\" \n";
        str += "when\n";
        str += "$a : Person( name == \"a\" )\n";
        str += "  (or $b : Person( name == \"b1\" )\n";
        str += "      (and $p : Person( name == \"p2\" )\n";
        str += "           $b : Person( name == \"b2\" ) )\n";
        str += "      (and $p : Person( name == \"p3\" )\n";
        str += "           $b : Person( name == \"b3\" ) )\n";
        str += "   )\n ";
        str += "then\n";
        str += "   mlist.add( $b );\n";
        str += "end\n";
        str += "rule rule2 dialect \"java\" \n";
        str += "when\n";
        str += "$a : Person( name == \"a\" )\n";
        str += "  (or $b : Person( name == \"b1\" )\n";
        str += "      (and $p : Person( name == \"p2\" )\n";
        str += "           $b : Person( name == \"b2\" ) )\n";
        str += "      (and $p : Person( name == \"p3\" )\n";
        str += "           $b : Person( name == \"b3\" ) )\n";
        str += "   )\n ";
        str += "then\n";
        str += "   jlist.add( $b );\n";
        str += "end\n";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(str));
        KieSession ksession = createKnowledgeSession(kbase);

        final Person a = new Person("a");
        final Person b1 = new Person("b1");
        final Person p2 = new Person("p2");
        final Person b2 = new Person("b2");
        final Person p3 = new Person("p3");
        final Person b3 = new Person("b3");

        final List mlist = new ArrayList();
        final List jlist = new ArrayList();

        ksession.setGlobal("mlist", mlist);
        ksession.setGlobal("jlist", jlist);
        ksession.insert(a);
        ksession.insert(b1);
        ksession.fireAllRules();
        assertEquals(b1, mlist.get(0));
        assertEquals(b1, jlist.get(0));

        ksession.dispose();
        ksession = createKnowledgeSession(kbase);
        ksession.setGlobal("mlist", mlist);
        ksession.setGlobal("jlist", jlist);
        ksession.insert(a);
        ksession.insert(b2);
        ksession.insert(p2);
        ksession.fireAllRules();
        assertEquals(b2, mlist.get(1));
        assertEquals(b2, jlist.get(1));

        ksession.dispose();
        ksession = createKnowledgeSession(kbase);
        ksession.setGlobal("mlist", mlist);
        ksession.setGlobal("jlist", jlist);
        ksession.insert(a);
        ksession.insert(b3);
        ksession.insert(p3);
        ksession.fireAllRules();
        assertEquals(b3, mlist.get(2));
        assertEquals(b3, jlist.get(2));
    }

    @Test
    public void testOrWithBinding() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_OrWithBindings.drl"));
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Person hola = new Person("hola");
        ksession.insert(hola);

        ksession.fireAllRules();

        assertEquals(0, list.size());
        final Cheese brie = new Cheese("brie");
        ksession.insert(brie);

        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains(hola));
        assertTrue(list.contains(brie));
    }

    @Test
    public void testOrWithFrom() {
        // JBRULES-2274: Rule does not fire as expected using deep object model and nested 'or' clause

        final String str = "package org.drools.compiler\n"
                + "rule NotContains\n"
                + "when\n"
                + "    $oi1 : OrderItem( )\n"
                + "    $o1  : Order(number == 1) from $oi1.order; \n"
                + "    ( eval(true) or eval(true) )\n"
                + "    $oi2 : OrderItem( )\n"
                + "    $o2  : Order(number == 2) from $oi2.order; \n"
                + "then\n"
                + "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Order order1 = new Order(1, "XYZ");
        final Order order2 = new Order(2, "ABC");
        final OrderItem item11 = new OrderItem(order1, 1);
        order1.addItem(item11);
        final OrderItem item21 = new OrderItem(order2, 1);
        order2.addItem(item21);

        ksession.insert(order1);
        ksession.insert(order2);
        ksession.insert(item11);
        ksession.insert(item21);

        final int rules = ksession.fireAllRules();
        assertEquals(2, rules);
    }

    @Test
    public void testOrWithReturnValueRestriction() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_OrWithReturnValue.drl");
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Cheese("brie", 18));
        ksession.insert(new Cheese("stilton", 8));
        ksession.insert(new Cheese("brie", 28));

        final int fired = ksession.fireAllRules();
        assertEquals(2, fired);
    }

    @Test
    public void testBindingsWithOr() throws InstantiationException, IllegalAccessException {
        // JBRULES-2917: matching of field==v1 || field==v2 breaks when variable binding is added

        final String str = "package org.drools.compiler\n" +
                "declare Assignment\n" +
                "    source : int\n" +
                "    target : int\n" +
                "end\n" +
                "rule ValueIsTheSame1\n" +
                "when\n" +
                "    Assignment( $t: target == 10 || target == source )\n" +
                "then\n" +
                "end\n" +
                "rule ValueIsTheSame2\n" +
                "when\n" +
                "    Assignment( $t: target == source || target == 10 )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final FactType asgType = kbase.getFactType("org.drools.compiler", "Assignment");
        final Object asg = asgType.newInstance();
        asgType.set(asg, "source", 10);
        asgType.set(asg, "target", 10);

        ksession.insert(asg);

        final int rules = ksession.fireAllRules();
        ksession.dispose();

        assertEquals(2, rules);
    }

    @Test
    public void testConstraintConnectorOr() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ConstraintConnectorOr.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);
        final List<Person> results = new ArrayList<Person>();
        ksession.setGlobal("results", results);

        final Person mark = new Person("Mark");
        mark.setAlive(true);
        mark.setHappy(true);

        final Person bush = new Person("Bush");
        bush.setAlive(true);
        bush.setHappy(false);

        final Person conan = new Person("Conan");
        conan.setAlive(false);
        conan.setHappy(true);

        final Person nero = new Person("Nero");
        nero.setAlive(false);
        nero.setHappy(false);

        ksession.insert(mark);
        ksession.insert(bush);
        ksession.insert(conan);
        ksession.insert(nero);

        ksession.fireAllRules();

        assertEquals(3, results.size());
        assertTrue(results.contains(mark));
        assertTrue(results.contains(bush));
        assertTrue(results.contains(conan));
    }

    @Test
    public void testVariableBindingWithOR() throws Exception {
        // JBRULES-3390
        final String str1 = "package org.drools.compiler.test; \n" +
                "declare A\n" +
                "end\n" +
                "declare B\n" +
                "   field : int\n" +
                "end\n" +
                "declare C\n" +
                "   field : int\n" +
                "end\n" +
                "rule R when\n" +
                "( " +
                "   A( ) and ( B( $bField : field ) or C( $cField : field ) ) " +
                ")\n" +
                "then\n" +
                "    System.out.println($bField);\n" +
                "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str1.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());

        final String str2 = "package org.drools.compiler.test; \n" +
                "declare A\n" +
                "end\n" +
                "declare B\n" +
                "   field : int\n" +
                "end\n" +
                "declare C\n" +
                "   field : int\n" +
                "end\n" +
                "rule R when\n" +
                "( " +
                "   A( ) and ( B( $field : field ) or C( $field : field ) ) " +
                ")\n" +
                "then\n" +
                "    System.out.println($field);\n" +
                "end\n";

        final KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add(ResourceFactory.newByteArrayResource(str2.getBytes()), ResourceType.DRL);
        assertFalse(kbuilder2.hasErrors());
    }

    @Test
    public void testRestrictionsWithOr() {
        // JBRULES-2203: NullPointerException When Using Conditional Element "or" in LHS Together with a Return Value Restriction

        final String str = "package org.drools.compiler\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    Cheese( price == (1 + 1) );\n" +
                "    (or eval(true);\n" +
                "        eval(true);\n" +
                "    )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Cheese("Stilton", 2));

        final int rules = ksession.fireAllRules();
        assertEquals(2, rules);
    }

    @Test
    public void testEmptyIdentifier() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_emptyIdentifier.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List result = new ArrayList();
        ksession.setGlobal("results", result);

        final Person person = new Person("bob");
        final Cheese cheese = new Cheese("brie", 10);

        ksession.insert(person);
        ksession.insert(cheese);

        ksession.fireAllRules();
        assertEquals(4, result.size());
    }

    @Test
    public void testIndexAfterOr() {
        // DROOLS-1604
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "  $p : Person(name == \"Mark\") or\n" +
                "  ( $mark : Person(name == \"Mark\")\n" +
                "    and\n" +
                "    $p : Person(age > $mark.age) )\n" +
                "  $s: String(this == $p.name)" +
                "then\n" +
                "  list.add($s);\n" +
                "end";

        KieSession ksession = new KieHelper().addContent( str, ResourceType.DRL ).build().newKieSession();

        ksession.insert( "Mario" );
        ksession.insert( "Edson" );
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "Mario", list.get(0) );
    }

}
