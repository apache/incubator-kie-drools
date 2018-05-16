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

package org.drools.compiler.integrationtests.drl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.FactC;
import org.drools.compiler.Message;
import org.drools.compiler.Order;
import org.drools.compiler.OrderItem;
import org.drools.compiler.Person;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.Sensor;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.integrationtests.facts.ClassA;
import org.drools.compiler.integrationtests.facts.ClassB;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PatternTest extends CommonTestMethodBase {

    @Test
    public void testDeclaringAndUsingBindsInSamePattern() throws IOException, ClassNotFoundException {
        final KieBaseConfiguration kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbc.setOption(RemoveIdentitiesOption.YES);
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(kbc, "test_DeclaringAndUsingBindsInSamePattern.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List sensors = new ArrayList();

        ksession.setGlobal("sensors", sensors);

        final Sensor sensor1 = new Sensor(100, 150);
        ksession.insert(sensor1);
        ksession.fireAllRules();
        assertEquals(0, sensors.size());

        final Sensor sensor2 = new Sensor(200, 150);
        ksession.insert(sensor2);
        ksession.fireAllRules();
        assertEquals(3, sensors.size());
    }

    @Test
    public void testEmptyPattern() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_EmptyPattern.drl");
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final Cheese stilton = new Cheese("stilton", 5);
        session.insert(stilton);

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();

        assertEquals(5, ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testPatternMatchingOnThis() throws Exception {
        final String rule = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "    $i1: Integer()\n" +
                "    $i2: Integer( this > $i1 )\n" +
                "then\n" +
                "   System.out.println( $i2 + \" > \" + $i1 );\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(1);
        ksession.insert(2);

        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testPatternOffset() throws Exception {
        // JBRULES-3427
        final String str = "package org.drools.compiler.test; \n" +
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
                "   A( ) or ( A( ) and B( ) ) " +
                ") and (\n" +
                "   A( ) or ( B( $bField : field ) and C( field != $bField ) )\n" +
                ")\n" +
                "then\n" +
                "    System.out.println(\"rule fired\");\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString( str );
        final KieSession ksession = kbase.newKieSession();

        final FactType typeA = kbase.getFactType( "org.drools.compiler.test", "A" );
        final FactType typeB = kbase.getFactType( "org.drools.compiler.test", "B" );
        final FactType typeC = kbase.getFactType( "org.drools.compiler.test", "C" );

        final Object a = typeA.newInstance();
        ksession.insert( a );

        final Object b = typeB.newInstance();
        typeB.set( b, "field", 1 );
        ksession.insert( b );

        final Object c = typeC.newInstance();
        typeC.set( c, "field", 1 );
        ksession.insert( c );

        ksession.fireAllRules();
    }

    @Test
    public void testPatternOnClass() throws Exception {
        final String rule = "import org.drools.core.reteoo.InitialFactImpl\n" +
                "import org.drools.compiler.FactB\n" +
                "rule \"Clear\" when\n" +
                "   $f: Object(class != FactB.class)\n" +
                "then\n" +
                "   if( ! ($f instanceof InitialFactImpl) ){\n" +
                "     delete( $f );\n" +
                "   }\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new FactA());
        ksession.insert(new FactA());
        ksession.insert(new FactB());
        ksession.insert(new FactB());
        ksession.insert(new FactC());
        ksession.insert(new FactC());
        ksession.fireAllRules();

        for (final FactHandle fact : ksession.getFactHandles()) {
            final InternalFactHandle internalFact = (InternalFactHandle) fact;
            assertTrue(internalFact.getObject() instanceof FactB);
        }
    }

    @Test
    public void testPredicateAsFirstPattern() throws Exception {
        final KieBase kbase = loadKnowledgeBase("predicate_as_first_pattern.drl");
        final KieSession ksession = kbase.newKieSession();

        final Cheese mussarela = new Cheese("Mussarela", 35);
        ksession.insert(mussarela);
        final Cheese provolone = new Cheese("Provolone", 20);
        ksession.insert(provolone);

        ksession.fireAllRules();

        assertEquals("The rule is being incorrectly fired", 35, mussarela.getPrice());
        assertEquals("Rule is incorrectly being fired", 20, provolone.getPrice());
    }

    @Test
    public void testConstantLeft() {
        // JBRULES-3627
        final String str = "import org.drools.compiler.*;\n" +
                "rule R1 when\n" +
                "   $p : Person( \"Mark\" == name )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person(null));
        ksession.insert(new Person("Mark"));

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testUppercaseField() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "global java.util.List list\n";
        rule += "declare Address\n";
        rule += "    Street: String\n";
        rule += "end\n";
        rule += "rule \"r1\"\n";
        rule += "when\n";
        rule += "    Address($street: Street)\n";
        rule += "then\n";
        rule += "    list.add($street);\n";
        rule += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = createKnowledgeSession(kbase);
        ksession.setGlobal("list", new ArrayList<String>());

        final FactType addressType = kbase.getFactType("org.drools.compiler.test", "Address");
        final Object address = addressType.newInstance();
        addressType.set(address, "Street", "5th Avenue");
        ksession.insert(address);
        ksession.fireAllRules();

        final List list = (List) ksession.getGlobal("list");
        assertEquals(1, list.size());
        assertEquals("5th Avenue", list.get(0));

        ksession.dispose();
    }

    @Test
    public void testUppercaseField2() throws Exception {
        final String rule = "package org.drools.compiler\n" +
                "declare SomeFact\n" +
                "    Field : String\n" +
                "    aField : String\n" +
                "end\n" +
                "rule X\n" +
                "when\n" +
                "    SomeFact( Field == \"foo\", aField == \"bar\" )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = createKnowledgeSession(kbase);

        final FactType factType = kbase.getFactType("org.drools.compiler", "SomeFact");
        final Object fact = factType.newInstance();
        factType.set(fact, "Field", "foo");
        factType.set(fact, "aField", "bar");
        ksession.insert(fact);

        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
        ksession.dispose();
    }

    @Test
    public void testHelloWorld() throws Exception {
        final KieBase kbase = loadKnowledgeBase("HelloWorld.drl");
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Message message = new Message("hola");
        message.addToList("hello");
        message.setNumber(42);

        ksession.insert(message);
        ksession.insert("boo");
        ksession.fireAllRules();
        assertTrue(message.isFired());
        assertEquals(message, ((List) ksession.getGlobal("list")).get(0));
    }

    @Test
    public void testBigDecimal() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("big_decimal_and_comparable.drl"));
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final PersonInterface bill = new Person("bill", null, 42);
        bill.setBigDecimal(new BigDecimal("42"));

        final PersonInterface ben = new Person("ben", null, 43);
        ben.setBigDecimal(new BigDecimal("43"));

        session.insert(bill);
        session.insert(new Cheese("gorgonzola", 43));
        session.insert(ben);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();

        assertEquals(1, ((List) session.getGlobal("list")).size());
    }

    @Test
    public void testSelfReference() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_SelfReference.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Order order = new Order(10, "Bob");
        final OrderItem item1 = new OrderItem(order, 1);
        final OrderItem item2 = new OrderItem(order, 2);
        final OrderItem anotherItem1 = new OrderItem(null, 3);
        final OrderItem anotherItem2 = new OrderItem(null, 4);
        ksession.insert(order);
        ksession.insert(item1);
        ksession.insert(item2);
        ksession.insert(anotherItem1);
        ksession.insert(anotherItem2);

        ksession.fireAllRules();

        assertEquals(2, results.size());
        assertTrue(results.contains(item1));
        assertTrue(results.contains(item2));
    }

    @Test
    public void testSelfReference2() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_SelfReference2.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);
        ksession.insert(new Cheese());
        ksession.fireAllRules();

        assertEquals(0, results.size());
    }

    @Test
    public void testImplicitDeclarations() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_implicitDeclarations.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);
        ksession.setGlobal("factor", 1.2);

        final Cheese cheese = new Cheese("stilton", 10);
        ksession.insert(cheese);

        ksession.fireAllRules();
        assertEquals(1, results.size());
    }

    @Test
    public void testMethodCalls() throws Exception {
        final String text = "package org.drools.compiler\n" +
                "rule \"method calls\"\n" +
                "when\n" +
                "    Person( getName().substring(2) == 'b' )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(text);
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert(new Person("mark", 50));
        int rules = ksession.fireAllRules();
        assertEquals(0, rules);

        ksession.insert(new Person("bob", 18));
        rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testSelfJoinWithIndex() throws IOException, ClassNotFoundException {
        String drl = "";
        drl += "package org.drools.compiler.test\n";
        drl += "import org.drools.compiler.Person\n";
        drl += "global java.util.List list\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   $p1 : Person( $name : name, $age : age )\n";
        drl += "   $p2 : Person( name == $name, age < $age)\n";
        drl += "then\n";
        drl += "    list.add( $p1 );\n";
        drl += "end\n";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(drl));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p1 = new Person("darth", 30);
        final FactHandle fh1 = ksession.insert(p1);

        final Person p2 = new Person("darth", 25);
        ksession.insert(p2); // creates activation.

        p1.setName("yoda");
        ksession.update(fh1, p1); // creates activation
        ksession.fireAllRules();

        assertEquals(0, list.size());
    }

    @Test
    public void testSelfJoinAndNotWithIndex() throws IOException, ClassNotFoundException {
        String drl = "";
        drl += "package org.drools.compiler.test\n";
        drl += "import org.drools.compiler.Person\n";
        drl += "global java.util.List list\n";
        drl += "rule test1\n";
        drl += "when\n";

        // selects the youngest person, for
        drl += "   $p1 : Person( )\n";
        drl += "     not Person( name == $p1.name, age < $p1.age )\n";

        // select the youngest person with the same name as $p1, but different likes and must be older
        drl += "   $p2 : Person( name == $p1.name, likes != $p1.likes, age > $p1.age)\n";
        drl += "     not Person( name == $p1.name, likes == $p2.likes, age < $p2.age )\n";
        drl += "then\n";
        drl += "    System.out.println( $p1 + \":\" + $p2 );\n";
        drl += "    list.add( $p1 );\n";
        drl += "    list.add( $p2 );\n";
        drl += "end\n";

        final KieBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( drl ) );
        final KieSession ksession = createKnowledgeSession( kbase );

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p0 = new Person("yoda", 0);
        p0.setLikes("cheddar");
        final FactHandle fh0 = ksession.insert(p0);

        final Person p1 = new Person("darth", 15);
        p1.setLikes("cheddar");
        final FactHandle fh1 = ksession.insert(p1);

        final Person p2 = new Person("darth", 25);
        p2.setLikes("cheddar");
        final FactHandle fh2 = ksession.insert(p2); // creates activation.

        final Person p3 = new Person("darth", 30);
        p3.setLikes("brie");
        final FactHandle fh3 = ksession.insert(p3);

        ksession.fireAllRules();
        // selects p1 and p3
        assertEquals(2, list.size());
        assertSame(p1, list.get(0));
        assertSame(p3, list.get(1));

        p1.setName("yoda");
        ksession.update(fh1, p1); // creates activation

        ksession.fireAllRules();
        // now selects p2 and p3
        assertEquals(4, list.size());
        assertSame(p2, list.get(2));
        assertSame(p3, list.get(3));
    }

    @Test
    public void testQualifiedFieldReference() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_QualifiedFieldReference.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Person bob = new Person("bob", "stilton");
        final Cheese stilton = new Cheese("stilton", 12);
        ksession.insert(bob);
        ksession.insert(stilton);

        ksession.fireAllRules();

        assertEquals(1, list.size());

        assertEquals(bob, list.get(0));
    }

    @Test
    public void testAutovivificationOfVariableRestrictions() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_AutoVivificationVR.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        ksession.insert(new Cheese("stilton", 10, 8));
        ksession.fireAllRules();
        assertEquals(1, results.size());
    }

    @Test
    public void testParentheses() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_ParenthesisUsage.drl");

        final List<Person> results = new ArrayList<>();
        final KieSession session = createKnowledgeSession(kbase);
        session.setGlobal("results", results);

        final Person bob = new Person("Bob", 20);
        bob.setAlive(true);
        final Person foo = new Person("Foo", 0);
        foo.setAlive(false);

        session.insert(bob);
        session.fireAllRules();

        assertEquals(1, results.size());
        assertEquals(bob, results.get(0));

        session.insert(foo);
        session.fireAllRules();

        assertEquals(2, results.size());
        assertEquals(foo, results.get(1));
    }

    @Test
    public void testCovariance() throws Exception {
        // JBRULES-3392
        final String str =
                        "import " + ClassA.class.getCanonicalName() + ";\n" +
                        "import " + ClassB.class.getCanonicalName() + ";\n" +
                        "rule x\n" +
                        "when\n" +
                        "   $b : ClassB( )\n" +
                        "   $a : ClassA( b.id == $b.id )\n" +
                        "then\n" +
                        "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString( str );
        final KieSession ksession = kbase.newKieSession();

        final ClassA a = new ClassA();
        final ClassB b = new ClassB();
        a.setB( b );

        ksession.insert( a );
        ksession.insert( b );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testCheckDuplicateVariables() throws Exception {
        // JBRULES-3035
        String str = "package com.sample\n" +
                "import org.drools.compiler.*\n" +
                "rule R1 when\n" +
                "   Person( $a: age, $a: name ) // this should cause a compile-time error\n" +
                "then\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());

        str = "package com.sample\n" +
                "rule R1 when\n" +
                "   accumulate( Object(), $c: count(1), $c: max(1) ) // this should cause a compile-time error\n" +
                "then\n" +
                "end";

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());

        str = "package com.sample\n" +
                "rule R1 when\n" +
                "   Number($i: intValue) from accumulate( Object(), $i: count(1) ) // this should cause a compile-time error\n" +
                "then\n" +
                "end";

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testCompilationFailureOnTernaryComparison() {
        // JBRULES-3642
        final String str =
                "declare Cont\n" +
                        "  val:Integer\n" +
                        "end\n" +
                        "rule makeFacts\n" +
                        "salience 10\n" +
                        "when\n" +
                        "then\n" +
                        "    insert( new Cont(2) );\n" +
                        "end\n" +
                        "rule R1\n" +
                        "when\n" +
                        "    $c: Cont( 3 < val < 10 )\n" +
                        "then\n" +
                        "end";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());
    }
}
