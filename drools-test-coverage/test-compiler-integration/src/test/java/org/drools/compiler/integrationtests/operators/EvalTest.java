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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.reteoo.ReteDumper;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.FactA;
import org.drools.testcoverage.common.model.FactB;
import org.drools.testcoverage.common.model.FactC;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class EvalTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EvalTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testEvalDefaultCompiler() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "global java.lang.Integer five;\n" +
                "rule \"eval rule test\"\n" +
                "    when\n" +
                "        $cheese : Cheese( $type:type == \"stilton\" )\n" +
                "        eval( $cheese.getPrice() == five.intValue() )\n" +
                "    then\n" +
                "        list.add( $cheese );\n" +
                "end";

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("eval-test", kieBaseTestConfiguration, drl);
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBase kbase;
        kbase = kieContainer.getKieBase();
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("five", 5);

            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Cheese stilton = new Cheese("stilton", 5);
            ksession.insert(stilton);
            ksession.fireAllRules();

            assertThat(((List) ksession.getGlobal("list")).get(0)).isEqualTo(stilton);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEvalNoPatterns() {
        final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                "global java.util.List list\n" +
                "rule \"no patterns1\"\n" +
                "    when\n" +
                "        eval(true);\n" +
                "    then\n" +
                "        list.add(\"fired1\");\n" +
                "end    \n" +
                "rule \"no patterns2\"\n" +
                "    when\n" +
                "        eval(false);\n" +
                "    then\n" +
                "        list.add(\"fired2\");\n" +
                "end \n" +
                "rule \"no patterns3\"\n" +
                "    when\n" +
                "        eval(true);\n" +
                "        eval(1==1);\n" +
                "    then\n" +
                "        list.add(\"fired3\");\n" +
                "end  \n" +
                "rule \"no patterns4\"\n" +
                "    when\n" +
                "        eval(false);\n" +
                "        eval(true);\n" +
                "        eval(1==1);\n" +
                "    then\n" +
                "        list.add(\"fired4\");\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();

            assertThat(list.contains("fired1")).isTrue();
            assertThat(list.contains("fired3")).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEvalMore() {

        final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list\n" +
                "rule \"another test\"\n" +
                "    when\n" +
                "        p : Person()\n" +
                "        eval(p.getName().equals(\"foo\") && p.getName().startsWith(\"f\"))\n" +
                "    then\n" +
                "        list.add( p );\n" +
                "end  \n" +
                "rule \"yet more\"\n" +
                "    when\n" +
                "        p : Person()\n" +
                "        eval(p.getName().equals(\"foo\") && p.getName().startsWith(\"f\"))\n" +
                "        eval(p.getName().equals(\"foo\") && p.getName().startsWith(\"q\"))        \n" +
                "    then\n" +
                "        list.add( p );\n" +
                "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);

            final Person foo = new Person("foo");
            session.insert(foo);
            session.fireAllRules();

            assertThat(((List) session.getGlobal("list")).get(0)).isEqualTo(foo);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testEvalCE() {
        final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"inline eval\"\n" +
                "when\n" +
                "    $str : String()\n" +
                "    $p   : Person()\n" +
                "    eval( $p.getName().startsWith($str) && $p.getName().endsWith($str) )" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            session.insert("b");

            session.insert(new Person("mark", 50));
            int rules = session.fireAllRules();
            assertThat(rules).isEqualTo(0);

            session.insert(new Person("bob", 18));
            rules = session.fireAllRules();
            assertThat(rules).isEqualTo(1);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testEvalException() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "function boolean throwException(Object object) {\n" +
                "    throw new Exception( \"this should throw an exception\" );\n" +
                "}\n" +
                "\n" +
                "rule \"Throw Eval Exception\"\n" +
                "    when\n" +
                "        cheese : Cheese( )\n" +
                "         eval( throwException( cheese ) )\n" +
                "    then\n" +
                "\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Cheese brie = new Cheese("brie", 12);
            try {
                ksession.insert(brie);
                ksession.fireAllRules();
                fail("Should throw an Exception from the Eval");
            } catch (final Exception e) {
                assertThat(e.getCause().getMessage().contains("this should throw an exception")).isTrue();
            }
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEvalInline() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"inline eval\"\n" +
                "when\n" +
                "    $str : String()\n" +
                "    Person( eval( name.startsWith($str) && age == 18) )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert("b");

            ksession.insert(new Person("mark", 50));
            int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(0);

            ksession.insert(new Person("bob", 18));
            rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEvalWithLineBreaks() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "\n" +
                "global java.util.List results\n" +
                "\n" +
                "function boolean testEqual( Object o1, Object o2 ) {\n" +
                "    return o1.equals(o2);\n" +
                "}\n" +
                "\n" +
                "rule \"TestRule\"\n" +
                "when\n" +
                "    $i : Integer( eval( testEqual( $i,\n" +
                "                              $i ) ) )\n" +
                "then\n" +
                "    results.add( $i );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List<Integer> results = new ArrayList<>();
            session.setGlobal("results", results);

            session.insert(10);
            session.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isEqualTo(10);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testEvalWithBigDecimal() {
        final String drl = 
                "package org.drools.compiler.integrationtests.operators;\n" +
                "import java.math.BigDecimal; \n" +
                "global java.util.List list \n" +
                "rule rule1 \n" +
                "    dialect \"java\" \n" +
                "when \n" +
                "    $bd : BigDecimal() \n" +
                "    eval( $bd.compareTo( BigDecimal.ZERO ) > 0 ) \n" +
                "then \n" +
                "    list.add( $bd ); \n" +
                "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);
            ksession.insert(new BigDecimal(1.5));

            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isEqualTo(new BigDecimal(1.5));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testFieldBiningsAndEvalSharing() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "// this is to test eval condition node sharing working properly\n" +
                "rule rule1\n" +
                "    when\n" +
                "        Person(val: name)\n" +
                "        eval(val == null)\n" +
                "    then\n" +
                "        list.add(\"rule1 fired\");\n" +
                "end    \n" +
                "\n" +
                "\n" +
                "rule rule2\n" +
                "    when\n" +
                "        Person(val: likes)\n" +
                "        eval(val == null) // note its the same guts, but different binding\n" +
                "    then\n" +
                "        list.add(\"rule2 fired\");\n" +
                "end ";
        evalSharingTest(drl);
    }

    @Test
    public void testFieldBiningsAndPredicateSharing() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "//this is to test eval condition node sharing working properly\n" +
                "rule rule1\n" +
                "    when\n" +
                "        Person(val: name, eval(val == null))\n" +
                "    then\n" +
                "        list.add(\"rule1 fired\");\n" +
                "end    \n" +
                "\n" +
                "\n" +
                "rule rule2\n" +
                "    when\n" +
                "        Person(val: likes, eval(val == null))\n" +
                "    then\n" +
                "        list.add(\"rule2 fired\");\n" +
                "end";
        evalSharingTest(drl);
    }

    private void evalSharingTest(final String drl) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        ReteDumper.dumpRete( ksession );
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Person tp1 = new Person();
            tp1.setName(null);
            tp1.setLikes("boo");
            ksession.insert(tp1);

            ksession.fireAllRules();

            assertThat(((List) ksession.getGlobal("list")).size()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testCastingInsideEvals() {
        final String drl = "package org.drools.compiler.integrationtests.operators\n" +
                "\n" +
                "global java.lang.Integer value;\n" +
                "\n" +
                "function boolean isEqual( Integer v1, Integer v2 ) {\n" +
                "    return v1.equals( v2 );\n" +
                "}\n" +
                "\n" +
                "rule \"test casts\"\n" +
                "when\n" +
                "    eval( isEqual((Integer) value, (Integer)value ) )\n" +
                "then\n" +
                "    // rule fired\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("value", 20);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAlphaEvalWithOrCE() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + FactA.class.getCanonicalName() + ";\n" +
                "import " + FactB.class.getCanonicalName() + ";\n" +
                "import " + FactC.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"test eval with OR\"\n" +
                "when\n" +
                "    FactA( eval( \"something\".equals( field1 ) ) )\n" +
                "    FactB() or FactC()\n" +
                "then\n" +
                "    results.add( \"Should not have fired\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            final FactA a = new FactA();
            a.setField1("a value");

            ksession.insert(a);
            ksession.insert(new FactB());
            ksession.insert(new FactC());

            ksession.fireAllRules();

            assertThat(list.size()).as("should not have fired").isEqualTo(0);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testModifyWithLiaToEval() {
        final String drl =
            "package org.drools.compiler.integrationtests.operators;\n" +
            "import " + Person.class.getCanonicalName() + "\n" +
            "global java.util.List list \n" +
            "rule xxx \n" +
            "when \n" +
            "    $p : Person() \n" +
            "    eval( $p.getAge() > 30 ) \n" +
            "then \n" +
            "  list.add($p); \n" +
            "end  \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Person p1 = new Person("darth", 25);
            final FactHandle fh = ksession.insert(p1);
            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(0);

            p1.setAge(35);
            ksession.update(fh, p1);
            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalWithFromAndEval() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
            "rule \"Test Rule\"\n" +
            "when\n" +
            "    $dec : java.math.BigDecimal() from java.math.BigDecimal.TEN;\n" +
            "    eval( $dec.compareTo(java.math.BigDecimal.ONE) > 0 )\n" +
            "then\n" +
            "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testPredicate() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "global java.lang.Integer two;\n" +
                "\n" +
                "rule \"predicate rule test\"\n" +
                "    when\n" +
                "        $person1 : Person( $age1 : age )\n" +
                "        // We have no autoboxing of primtives, so have to do by hand\n" +
                "        person2 : Person( $age2:age, eval( $age2 == ( $age1 + two.intValue() ) ) )\n" +
                "    then\n" +
                "        list.add( $person1 );\n" +
                "        list.add( person2 );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.setGlobal("two", 2);

            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Person peter = new Person("peter", null, 12);
            ksession.insert(peter);
            final Person jane = new Person("jane", null, 10);
            ksession.insert(jane);
            ksession.fireAllRules();

            assertThat(((List) ksession.getGlobal("list")).get(0)).isEqualTo(jane);
            assertThat(((List) ksession.getGlobal("list")).get(1)).isEqualTo(peter);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testPredicateException() {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "function boolean throwException(Object object) {\n" +
                "    throw new RuntimeException( \"this should throw an exception\" );\n" +
                "}\n" +
                "\n" +
                "rule \"Throw Predicate Exception\"\n" +
                "    when\n" +
                "        Cheese( type1:type, eval( throwException( type1 ) ) )\n" +
                "    then\n" +
                "\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("eval-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Cheese brie = new Cheese("brie", 12);
            try {
                ksession.insert(brie);
                ksession.fireAllRules();
                fail("Should throw an Exception from the Predicate");
            } catch (final Exception e) {
                Throwable cause = e.getCause();
                if (cause instanceof InvocationTargetException) {
                    cause = ((InvocationTargetException) cause).getTargetException();
                }
                assertThat(cause.getMessage().contains("this should throw an exception")).isTrue();
            }
        } finally {
            ksession.dispose();
        }
    }
}
