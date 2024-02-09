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
package org.drools.compiler.integrationtests.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.FactA;
import org.drools.testcoverage.common.model.FactB;
import org.drools.testcoverage.common.model.Order;
import org.drools.testcoverage.common.model.OrderItem;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class OrTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OrTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testOr() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"literal test rule\"\n" +
                "    when\n" +
                "        Cheese(type == \"stilton\" ) or Cheese(type == \"cheddar\")\n" +
                "    then\n" +
                "        list.add(\"got cheese\");\n" +
                "end   \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Cheese cheddar = new Cheese("cheddar", 5);
            final FactHandle h = session.insert(cheddar);

            session.fireAllRules();

            // just one added
            assertThat(list.get(0)).isEqualTo("got cheese");
            assertThat(list.size()).isEqualTo(1);

            session.delete(h);
            session.fireAllRules();

            // still just one
            assertThat(list.size()).isEqualTo(1);

            session.insert(new Cheese("stilton", 5));
            session.fireAllRules();

            // now have one more
            assertThat(((List) session.getGlobal("list")).size()).isEqualTo(2);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testOrCE() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule \"test or CE\"\n" +
                "when\n" +
                "    $c : Cheese( type == \"stilton\" )\n" +
                "    or\n" +
                "    (\n" +
                "        $c2 : Cheese( type == \"brie\" )\n" +
                "        and\n" +
                "        (\n" +
                "            $p : Person( likes == \"stilton\" )\n" +
                "            or\n" +
                "            $p : Person( name == \"bob\" )\n" +
                "        )\n" +
                "    ) \n" +
                "then\n" +
                "    results.add(\" OK \" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            ksession.insert(new Cheese("brie", 10));
            ksession.insert(new Person("bob"));

            ksession.fireAllRules();

            assertThat(list.size()).as("should have fired once").isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOrCEFollowedByEval() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + FactA.class.getCanonicalName() + ";\n" +
                "import " + FactB.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"test eval with OR\"\n" +
                "when\n" +
                "    FactA( $a : field1 )\n" +
                "    $f : FactB( $b : f1 ) or $f : FactB( $b : f1 == \"X\" )\n" +
                "    eval( $a.equals( $b ) ) \n" +
                "then\n" +
                "    results.add( $f );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            ksession.insert(new FactA("X"));
            final InternalFactHandle b = (InternalFactHandle) ksession.insert(new FactB("X"));

            ksession.fireAllRules();

            assertThat(list.size()).as("should have fired").isEqualTo(2);
            assertThat(list.contains(b.getObject())).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOrWithAndUsingNestedBindings() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List mlist\n" +
                "global java.util.List jlist\n" +
                "rule rule1 dialect \"mvel\" \n" +
                "when\n" +
                "$a : Person( name == \"a\" )\n" +
                "  (or $b : Person( name == \"b1\" )\n" +
                "      (and $p : Person( name == \"p2\" )\n" +
                "           $b : Person( name == \"b2\" ) )\n" +
                "      (and $p : Person( name == \"p3\" )\n" +
                "           $b : Person( name == \"b3\" ) )\n" +
                "   )\n " +
                "then\n" +
                "   mlist.add( $b );\n" +
                "end\n" +
                "rule rule2 dialect \"java\" \n" +
                "when\n" +
                "$a : Person( name == \"a\" )\n" +
                "  (or $b : Person( name == \"b1\" )\n" +
                "      (and $p : Person( name == \"p2\" )\n" +
                "           $b : Person( name == \"b2\" ) )\n" +
                "      (and $p : Person( name == \"p3\" )\n" +
                "           $b : Person( name == \"b3\" ) )\n" +
                "   )\n " +
                "then\n" +
                "   jlist.add( $b );\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final List mlist = new ArrayList();
        final List jlist = new ArrayList();
        final Person a = new Person("a");
        final Person b1 = new Person("b1");
        final Person p2 = new Person("p2");
        final Person b2 = new Person("b2");
        final Person p3 = new Person("p3");
        final Person b3 = new Person("b3");
        KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("mlist", mlist);
            ksession.setGlobal("jlist", jlist);
            ksession.insert(a);
            ksession.insert(b1);
            ksession.fireAllRules();
            assertThat(mlist.get(0)).isEqualTo(b1);
            assertThat(jlist.get(0)).isEqualTo(b1);
        } finally {
            ksession.dispose();
        }

        ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("mlist", mlist);
            ksession.setGlobal("jlist", jlist);
            ksession.insert(a);
            ksession.insert(b2);
            ksession.insert(p2);
            ksession.fireAllRules();
            assertThat(mlist.get(1)).isEqualTo(b2);
            assertThat(jlist.get(1)).isEqualTo(b2);
        } finally {
            ksession.dispose();
        }

        ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("mlist", mlist);
            ksession.setGlobal("jlist", jlist);
            ksession.insert(a);
            ksession.insert(b3);
            ksession.insert(p3);
            ksession.fireAllRules();
            assertThat(mlist.get(2)).isEqualTo(b3);
            assertThat(jlist.get(2)).isEqualTo(b3);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOrWithBinding() {

        final String drl =  "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results\n" +
                "\n" +
                "rule \"MyRule\"\n" +
                "    when\n" +
                "        c : (Cheese( type == \"stilton\") or\n" +
                "             Cheese( type == \"brie\" ) or\n" +
                "             Cheese( type == \"muzzarella\" ) )\n" +
                "        p : Person()\n" +
                "    then\n" +
                "        results.add(c);\n" +
                "        results.add(p);\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            final Person hola = new Person("hola");
            ksession.insert(hola);

            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(0);
            final Cheese brie = new Cheese("brie");
            ksession.insert(brie);

            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(2);
            assertThat(list.contains(hola)).isTrue();
            assertThat(list.contains(brie)).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOrWithFrom() {
        // JBRULES-2274: Rule does not fire as expected using deep object model and nested 'or' clause
        final String drl = "package org.drools.compiler.integrationtests.operators;\n"
                + "import " + Order.class.getCanonicalName() + ";\n"
                + "import " + OrderItem.class.getCanonicalName() + ";\n"
                + "rule NotContains\n"
                + "when\n"
                + "    $oi1 : OrderItem( )\n"
                + "    $o1  : Order(number == 1) from $oi1.order; \n"
                + "    ( eval(true) or eval(true) )\n"
                + "    $oi2 : OrderItem( )\n"
                + "    $o2  : Order(number == 2) from $oi2.order; \n"
                + "then\n"
                + "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
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
            assertThat(rules).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOrWithReturnValueRestriction() {

        final String drl =
                "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                " \n" +
                "rule \"r1\"\n" +
                "when\n" +
                "    Cheese( type == \"brie\", $price : price )\n" +
                "    Cheese( type == \"stilton\", price == 10 || == ( $price % 10 ) )\n" +
                "then\n" +
                "    // noop\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Cheese("brie", 18));
            ksession.insert(new Cheese("stilton", 8));
            ksession.insert(new Cheese("brie", 28));

            final int fired = ksession.fireAllRules();
            assertThat(fired).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBindingsWithOr() throws InstantiationException, IllegalAccessException {
        // JBRULES-2917: matching of field==v1 || field==v2 breaks when variable binding is added
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
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


        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactType asgType = kbase.getFactType("org.drools.compiler.integrationtests.operators", "Assignment");
            final Object asg = asgType.newInstance();
            asgType.set(asg, "source", 10);
            asgType.set(asg, "target", 10);

            ksession.insert(asg);

            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testConstraintConnectorOr() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results\n" +
                "\n" +
                "rule \"Simple || operator\"\n" +
                "    when\n" +
                "        $person : Person( alive == true || happy == true )\n" +
                "    then\n" +
                "        results.add( $person );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Person> results = new ArrayList<>();
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

            assertThat(results.size()).isEqualTo(3);
            assertThat(results.contains(mark)).isTrue();
            assertThat(results.contains(bush)).isTrue();
            assertThat(results.contains(conan)).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testVariableBindingWithOR() throws Exception{
        // JBRULES-3390
        final String drl1 = "package org.drools.compiler.integrationtests.operators; \n" +
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

        KieBuilder kbuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl1);
        assertThat(kbuilder.getResults().getMessages().isEmpty()).isFalse();

        final String drl2 = "package org.drools.compiler.integrationtests.operators; \n" +
                "global java.util.List results\n" +
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
                "    results.add($field); "+
                "end\n";

        kbuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl2);
        assertThat(kbuilder.getResults().getMessages().isEmpty()).isTrue();

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl2);
        final KieSession ksession = kbase.newKieSession();
        try {

            FactType aType = kbase.getFactType("org.drools.compiler.integrationtests.operators", "A");
            Object aInstance = aType.newInstance();
            ksession.insert(aInstance);

            FactType cType = kbase.getFactType("org.drools.compiler.integrationtests.operators", "C");
            Object cInstance = cType.newInstance();
            cType.set(cInstance, "field", 5);
            ksession.insert(cInstance);

            final List<Integer> results = new ArrayList<>();
            ksession.setGlobal("results", results);

            ksession.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
            assertThat(results.contains(5)).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testRestrictionsWithOr() {
        // JBRULES-2203: NullPointerException When Using Conditional Element "or" in LHS Together with a Return Value Restriction
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    Cheese( price == (1 + 1) );\n" +
                "    (or eval(true);\n" +
                "        eval(true);\n" +
                "    )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Cheese("Stilton", 2));

            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEmptyIdentifier() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule \"Or condition followed by fact\"\n" +
                "  when\n" +
                "    Cheese( type == \"stilton\" ) or Cheese( type == \"brie\" )\n" +
                "    Person( )\n" +
                "  then\n" +
                "    results.add(\"Or condition followed by fact is ok\");\n" +
                "end\n" +
                "\n" +
                "rule \"Fact followed by or condition\"\n" +
                "  when\n" +
                "    Person( )\n" +
                "    Cheese( type == \"stilton\" ) or Cheese( type == \"brie\" )\n" +
                "  then\n" +
                "    results.add(\"Fact followed by or condition is ok\");\n" +
                "end\n" +
                "\n" +
                "rule \"Single fact\"\n" +
                "  when\n" +
                "    Person( )\n" +
                "  then\n" +
                "    results.add(\"Single fact is ok\");\n" +
                "end\n" +
                "\n" +
                "rule \"Single or\"\n" +
                "  when\n" +
                "    Cheese( type == \"stilton\" ) or Cheese( type == \"brie\" )\n" +
                "  then\n" +
                "    results.add(\"Single or is ok\");\n" +
                "end";
        
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List result = new ArrayList();
            ksession.setGlobal("results", result);

            final Person person = new Person("bob");
            final Cheese cheese = new Cheese("brie", 10);

            ksession.insert(person);
            ksession.insert(cheese);

            ksession.fireAllRules();
            assertThat(result.size()).isEqualTo(4);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testIndexAfterOr() {
        // DROOLS-1604
        final String drl =
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


        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert( "Mario" );
            ksession.insert( "Edson" );
            ksession.insert(new Person("Mark", 37));
            ksession.insert(new Person("Edson", 35));
            ksession.insert(new Person("Mario", 40));

            final List<String> list = new ArrayList<>();
            ksession.setGlobal( "list", list );

            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isEqualTo("Mario");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOrWithDifferenceOffsetsForConsequence() {
        // DROOLS-1604
        final String drl =
              "import " + Person.class.getCanonicalName() + ";" +
              "global java.util.List list\n" +
              "rule R dialect \"mvel\" when\n" +
              "  ( $p : Person(name == \"Mark\")" +
              "    or\n" +
              "    $s : String() and $p : Person(name == $s) )\n" +
              "then\n" +
              "  list.add($p.getName());\n" +
              "end";


        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("or-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert( "Mark" );
            ksession.insert(new Person("Mark", 37));

            final List<String> list = new ArrayList<>();
            ksession.setGlobal( "list", list );

            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(2);
            assertThat(list.get(0)).isEqualTo("Mark");
            assertThat(list.get(1)).isEqualTo("Mark");
        } finally {
            ksession.dispose();
        }
    }

}
