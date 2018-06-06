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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.InitialFactImpl;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.ClassA;
import org.drools.testcoverage.common.model.ClassB;
import org.drools.testcoverage.common.model.FactA;
import org.drools.testcoverage.common.model.FactB;
import org.drools.testcoverage.common.model.FactC;
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.model.Order;
import org.drools.testcoverage.common.model.OrderItem;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Primitives;
import org.drools.testcoverage.common.model.Sensor;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class PatternTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PatternTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testDeclaringAndUsingBindsInSamePattern() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Sensor.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List sensors;\n" +
                "\n" +
                "rule \"BindsTest1_returnValue\"\n" +
                "    when\n" +
                "        $sensor1 : Sensor( $temp1 : temperature, pressure < $temp1 )\n" +
                "        $sensor2 : Sensor( $temp2 : temperature, pressure < ( $temp1 + $temp2 ) )\n" +
                "    then\n" +
                "        sensors.add( $sensor1 );\n" +
                "end\n" +
                "\n" +
                "rule \"BindsTest2_predicate\"\n" +
                "    when\n" +
                "        $sensor1 : Sensor( $temp1 : temperature, pressure < $temp1 )\n" +
                "        $sensor2 : Sensor( $temp2 : temperature, $p : pressure,  eval ( $p < ($temp1 + $temp2 ) ) )\n" +
                "    then\n" +
                "        sensors.add( $sensor1 );\n" +
                "end\n" +
                "\n" +
                "rule \"BindsTest3_eval\"\n" +
                "    when\n" +
                "        $sensor1 : Sensor( $temp1 : temperature, pressure < $temp1 )\n" +
                "        $sensor2 : Sensor( $temp2 : temperature, $p : pressure )\n" +
                "        eval( $p < $temp1 + $temp2 )\n" +
                "    then\n" +
                "        sensors.add( $sensor1 );\n" +
                "end";

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("pattern-test", kieBaseTestConfiguration, drl);
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBaseConfiguration kieBaseConfiguration = kieBaseTestConfiguration.getKieBaseConfiguration();
        kieBaseConfiguration.setOption(RemoveIdentitiesOption.YES);
        final KieBase kbase = kieContainer.newKieBase(kieBaseConfiguration);
        final KieSession ksession = kbase.newKieSession();
        try {
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
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEmptyPattern() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                " \n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                " \n" +
                "rule \"simple rule\"\n" +
                "    when\n" +
                "        cheese : Cheese( )\n" +
                "    then\n" +
                "        list.add( new Integer(5) );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Cheese stilton = new Cheese("stilton", 5);
            session.insert(stilton);
            session.fireAllRules();

            assertEquals(5, ((List) session.getGlobal("list")).get(0));
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testPatternMatchingOnThis() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "rule R1 when\n" +
                "    $i1: Integer()\n" +
                "    $i2: Integer( this > $i1 )\n" +
                "then\n" +
                "   System.out.println( $i2 + \" > \" + $i1 );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(1);
            ksession.insert(2);

            final int rules = ksession.fireAllRules();
            assertEquals(1, rules);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testPatternOffset() throws Exception {
        // JBRULES-3427
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
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

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactType typeA = kbase.getFactType( "org.drools.compiler.integrationtests.drl", "A" );
            final FactType typeB = kbase.getFactType( "org.drools.compiler.integrationtests.drl", "B" );
            final FactType typeC = kbase.getFactType( "org.drools.compiler.integrationtests.drl", "C" );

            final Object a = typeA.newInstance();
            ksession.insert( a );

            final Object b = typeB.newInstance();
            typeB.set( b, "field", 1 );
            ksession.insert( b );

            final Object c = typeC.newInstance();
            typeC.set( c, "field", 1 );
            ksession.insert( c );

            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testPatternOnClass() {
        final String drl = "import " + InitialFactImpl.class.getCanonicalName() + "\n" +
                "import " + FactB.class.getCanonicalName() + "\n" +
                "rule \"Clear\" when\n" +
                "   $f: Object(class != FactB.class)\n" +
                "then\n" +
                "   if( ! ($f instanceof InitialFactImpl) ){\n" +
                "     delete( $f );\n" +
                "   }\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
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
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testPredicateAsFirstPattern() {

        final String drl = "package oreg.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"Using Predicate as first pattern\"\n" +
                "  when\n" +
                "    cheese: Cheese( type == \"Mussarela\", $price:price, eval( $price < 30 ))\n" +
                "  then\n" +
                "    cheese.setPrice(40);\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Cheese mussarela = new Cheese("Mussarela", 35);
            ksession.insert(mussarela);
            final Cheese provolone = new Cheese("Provolone", 20);
            ksession.insert(provolone);

            ksession.fireAllRules();

            assertEquals("The rule is being incorrectly fired", 35, mussarela.getPrice());
            assertEquals("Rule is incorrectly being fired", 20, provolone.getPrice());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testConstantLeft() {
        // JBRULES-3627
        final String drl = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "   $p : Person( \"Mark\" == name )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Person(null));
            ksession.insert(new Person("Mark"));

            assertEquals(1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testUppercaseField() throws Exception {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
            "global java.util.List list\n" +
            "declare Address\n" +
            "    Street: String\n" +
            "end\n" +
            "rule \"r1\"\n" +
            "when\n" +
            "    Address($street: Street)\n" +
            "then\n" +
            "    list.add($street);\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("list", new ArrayList<String>());

            final FactType addressType = kbase.getFactType("org.drools.compiler.integrationtests.drl", "Address");
            final Object address = addressType.newInstance();
            addressType.set(address, "Street", "5th Avenue");
            ksession.insert(address);
            ksession.fireAllRules();

            final List list = (List) ksession.getGlobal("list");
            assertEquals(1, list.size());
            assertEquals("5th Avenue", list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testUppercaseField2() throws Exception {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "declare SomeFact\n" +
                "    Field : String\n" +
                "    aField : String\n" +
                "end\n" +
                "rule X\n" +
                "when\n" +
                "    SomeFact( Field == \"foo\", aField == \"bar\" )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactType factType = kbase.getFactType("org.drools.compiler.integrationtests.drl", "SomeFact");
            final Object fact = factType.newInstance();
            factType.set(fact, "Field", "foo");
            factType.set(fact, "aField", "bar");
            ksession.insert(fact);

            final int rules = ksession.fireAllRules();
            assertEquals(1, rules);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testHelloWorld() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                " \n" +
                "//we don't use the import, as class is fully qualified below\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Hello World\"\n" +
                "    when\n" +
                "        $m : " + Message.class.getCanonicalName() + "(list contains \"hello\",\n" +
                "                                text:message, message == \"hola\",\n" +
                "                                fired == false,\n" +
                "                                number > 40,\n" +
                "                                birthday > \"10-Jul-1974\",\n" +
                "                                message matches \".*ho.*\",\n" +
                "                                list excludes \"wax\")\n" +
                "    then\n" +
                "        // putting in a complex consequence, to make sure it picks up the variabels correctly\n" +
                "        if (1==1)  {\n" +
                "            int a = 0;\n" +
                "        }\n" +
                "        try {\n" +
                "            //System.out.println(\"hello world with collections \" + $m.getMessage());\n" +
                "        } catch  ( Exception e ) {\n" +
                "\n" +
                "        } finally {\n" +
                "            list.add( $m );\n" +
                "        }\n" +
                "        modify($m) { setFired(true) }\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
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
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimal() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Primitives.class.getCanonicalName() + ";\n" +
                "import java.math.BigDecimal;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"BigDec\"\n" +
                "\n" +
                "    when\n" +
                "        Cheese($price : price)\n" +
                "        p : Primitives(bigDecimal < $price)\n" +
                "    then\n" +
                "        list.add( p );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Primitives bill = new Primitives();
            bill.setBigDecimal(new BigDecimal("42"));

            final Primitives ben = new Primitives();
            ben.setBigDecimal(new BigDecimal("43"));

            session.insert(bill);
            session.insert(new Cheese("gorgonzola", 43));
            session.insert(ben);
            session.fireAllRules();

            assertEquals(1, ((List) session.getGlobal("list")).size());
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testSelfReference() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Order.class.getCanonicalName() + ";\n" +
                "import " + OrderItem.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"reversed references\"\n" +
                "when\n" +
                "    $item : OrderItem( $order : order )\n" +
                "    Order( this == $order )\n" +
                "then\n" +
                "    results.add( $item );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
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
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testSelfReference2() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"reversed references\"\n" +
                "when\n" +
                "    $cheese : Cheese( )\n" +
                "    Cheese( this != $cheese )\n" +
                "then\n" +
                "    results.add( $cheese );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);
            ksession.insert(new Cheese());
            ksession.fireAllRules();

            assertEquals(0, results.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testImplicitDeclarations() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "global java.lang.Double factor;\n" +
                "\n" +
                "rule \"test implicit declarations\"\n" +
                "    when\n" +
                "         // implicit binding\n" +
                "        Cheese( type == \"stilton\", eval( price < 20*factor ) )\n" +
                "        // late declaration\n" +
                "        Cheese( price < ( price * factor ), eval( price < price * factor ), price : price  )\n" +
                "    then\n" +
                "        results.add( \"Rule Fired\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);
            ksession.setGlobal("factor", 1.2);

            final Cheese cheese = new Cheese("stilton", 10);
            ksession.insert(cheese);

            ksession.fireAllRules();
            assertEquals(1, results.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMethodCalls() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"method calls\"\n" +
                "when\n" +
                "    Person( getName().substring(2) == 'b' )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Person("mark", 50));
            int rules = ksession.fireAllRules();
            assertEquals(0, rules);

            ksession.insert(new Person("bob", 18));
            rules = ksession.fireAllRules();
            assertEquals(1, rules);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testSelfJoinWithIndex() {
        final String drl =
            "package org.drools.compiler.integrationtests.drl;\n" +
            "import " + Person.class.getCanonicalName() + ";\n" +
            "global java.util.List list\n" +
            "rule test1\n" +
            "when\n" +
            "   $p1 : Person( $name : name, $age : age )\n" +
            "   $p2 : Person( name == $name, age < $age)\n" +
            "then\n" +
            "    list.add( $p1 );\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
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
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testSelfJoinAndNotWithIndex() {
        final String drl =
            "package org.drools.compiler.integrationtests.drl;\n" +
            "import " + Person.class.getCanonicalName() + ";\n" +
            "global java.util.List list\n" +
            "rule test1\n" +
            "when\n" +
            "   $p1 : Person( )\n" +
            "     not Person( name == $p1.name, age < $p1.age )\n" +
            "   $p2 : Person( name == $p1.name, likes != $p1.likes, age > $p1.age)\n" +
            "     not Person( name == $p1.name, likes == $p2.likes, age < $p2.age )\n" +
            "then\n" +
            "    System.out.println( $p1 + \":\" + $p2 );\n" +
            "    list.add( $p1 );\n" +
            "    list.add( $p2 );\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Person p0 = new Person("yoda", 0);
            p0.setLikes("cheddar");
            ksession.insert(p0);

            final Person p1 = new Person("darth", 15);
            p1.setLikes("cheddar");
            final FactHandle fh1 = ksession.insert(p1);

            final Person p2 = new Person("darth", 25);
            p2.setLikes("cheddar");
            ksession.insert(p2); // creates activation.

            final Person p3 = new Person("darth", 30);
            p3.setLikes("brie");
            ksession.insert(p3);

            ksession.fireAllRules();
            // selects p1 and p3
            if (kieBaseTestConfiguration == KieBaseTestConfiguration.CLOUD_IDENTITY) {
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
            } else {
                // Person has equals method based on the Person's name.
                // There are 3 Darths, so 2 of them are not inserted with EQUALITY.
                assertEquals(0, list.size());

                p1.setName("yoda");
                ksession.update(fh1, p1);

                ksession.fireAllRules();
                assertEquals(0, list.size());
            }
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testQualifiedFieldReference() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"test qualified field reference\"\n" +
                "when\n" +
                "    $p : Person( $p.name == \"bob\" );\n" +
                "    $c : Cheese( $c.type == $p.likes )\n" +
                "then\n" +
                "    results.add( $p );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            final Person bob = new Person("bob");
            bob.setLikes("stilton");
            final Cheese stilton = new Cheese("stilton", 12);
            ksession.insert(bob);
            ksession.insert(stilton);

            ksession.fireAllRules();

            assertEquals(1, list.size());

            assertEquals(bob, list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAutovivificationOfVariableRestrictions() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"autovivification\"\n" +
                "when\n" +
                "     Cheese( price > oldPrice, price > this.oldPrice )\n" +
                "then\n" +
                "     results.add( \"OK\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            ksession.setGlobal("results", results);

            final Cheese stilton = new Cheese("stilton");
            stilton.setPrice(10);
            stilton.setOldPrice(8);
            ksession.insert(stilton);

            ksession.fireAllRules();
            assertEquals(1, results.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testParentheses() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results\n" +
                "\n" +
                "rule \"TestRule\"\n" +
                "when\n" +
                "    $p : Person( alive ==true ||(alive==false && age ==0) )\n" +
                "then\n" +
                "    results.add( $p );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List<Person> results = new ArrayList<>();
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
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testCovariance() {
        // JBRULES-3392
        final String drl =
                        "import " + ClassA.class.getCanonicalName() + ";\n" +
                        "import " + ClassB.class.getCanonicalName() + ";\n" +
                        "rule x\n" +
                        "when\n" +
                        "   $b : ClassB( )\n" +
                        "   $a : ClassA( b.id == $b.id )\n" +
                        "then\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("pattern-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final ClassA a = new ClassA();
            final ClassB b = new ClassB();
            a.setB( b );

            ksession.insert( a );
            ksession.insert( b );
            assertEquals( 1, ksession.fireAllRules() );
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testCheckDuplicateVariables() {
        // JBRULES-3035
        String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "   Person( $a: age, $a: name ) // this should cause a compile-time error\n" +
                "then\n" +
                "end";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        Assertions.assertThat(kieBuilder.getResults().getMessages()).extracting(org.kie.api.builder.Message::getText).doesNotContain("");

        drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "rule R1 when\n" +
                "   accumulate( Object(), $c: count(1), $c: max(1) ) // this should cause a compile-time error\n" +
                "then\n" +
                "end";

        kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        Assertions.assertThat(kieBuilder.getResults().getMessages()).extracting(org.kie.api.builder.Message::getText).doesNotContain("");

        drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "rule R1 when\n" +
                "   Number($i: intValue) from accumulate( Object(), $i: count(1) ) // this should cause a compile-time error\n" +
                "then\n" +
                "end";

        kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        Assertions.assertThat(kieBuilder.getResults().getMessages()).extracting(org.kie.api.builder.Message::getText).doesNotContain("");
    }

    @Test
    public void testCompilationFailureOnTernaryComparison() {
        // JBRULES-3642
        final String drl =
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

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        Assertions.assertThat(kieBuilder.getResults().getMessages()).extracting(org.kie.api.builder.Message::getText).doesNotContain("");
    }
}
