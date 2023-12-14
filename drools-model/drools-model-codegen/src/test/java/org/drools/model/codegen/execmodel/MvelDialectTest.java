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
package org.drools.model.codegen.execmodel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.WorkingMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Counter;
import org.drools.model.codegen.execmodel.domain.InternationalAddress;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.ValueHolder;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

public class MvelDialectTest extends BaseModelTest {

    public MvelDialectTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testMVELinsert() {
        String str = "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Integer()\n" +
                "then\n" +
                "  System.out.println(\"Hello World\");\n" +
                "  insert(\"Hello World\");\n" +
                "end";

        KieSession ksession = getKieSession(str);

        FactHandle fh_47 = ksession.insert(47);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.contains("Hello World")).isTrue();
    }

    @Test
    public void testMVELMapSyntax() {
        final String drl = "" +
                "import java.util.*;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "dialect \"mvel\"\n" +
                "\n" +
                "rule \"rule1\"\n" +
                "  when\n" +
                "    m: Person($name: name , " +
                "              $status : \"value1\", " +
                "              $key1 : \"key1\", " +
                "              $key2 : \"key2\", " +
                "              $value4 : 2 " +
                ")\n" +
                "  then\n" +
                "    m.itemsString[$key1] = $status;\n" +
                "    m.itemsString[$key2] = \"value2\";\n" +
                "    m.itemsString[\"key3\"] = \"value3\";\n" +
                "    m.getItemsString().put( $key1, $status );\n" +
                "    m.getItemsString().put( $key2, \"value2\" );\n" +
                "    m.getItemsString().put( \"key3\", \"value2\" );\n" +
                "    m.getItemsString().put( \"key4\", $value4 );\n" +
                "    update(m);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person p = new Person("Luca");

        ksession.insert(p);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Map<String, String> itemsString = p.getItemsString();

        assertThat(itemsString.keySet().size()).isEqualTo(4);
    }

    @Test
    public void testMVELmodify() {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify($p) { setAge(1); }\n" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Matteo", 47));
        ksession.fireAllRules();

        Collection<Person> results = getObjectsIntoList(ksession, Person.class);
        assertThat(results.iterator().next().getAge()).isEqualTo(1);
        results.forEach(System.out::println);
    }

    @Test
    public void testMVELmultiple() {
        String str = "package mypackage;" +
                "dialect \"mvel\"\n" + // MVEL dialect defined at package level.
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "when\n" +
                "  Integer()\n" +
                "then\n" +
                "  System.out.println(\"Hello World\")\n" + // no ending ; as per MVEL dialect
                "  insert(new Person(\"Matteo\", 47))\n" +
                "  insert(\"Hello World\")\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify($p) { setAge(1); }\n" +
                "  insert(\"Modified person age to 1 for: \"+$p.name)\n" + // Please notice $p.name is MVEL dialect.
                "end\n" +
                "rule R3\n" +
                "when\n" +
                "  $s : String( this == \"Hello World\")\n" +
                "  $p : Person()\n" + // this is artificially added to ensure working even with unnecessary declaration passed to on().execute().
                "then\n" +
                "  retract($s)" +
                "end\n";

        KieSession ksession = getKieSession(str);

        FactHandle fh_47 = ksession.insert(47);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        System.out.println(results);
        assertThat(results.contains("Hello World")).isFalse();
        assertThat(results.contains("Modified person age to 1 for: Matteo")).isTrue();
    }

    @Test
    public void testMVELmultipleStatements() {
        String str =
                "import " + Person.class.getPackage().getName() + ".*;\n" + // keep the package.* in order for Address to be resolvable in the RHS.
                        "rule R\n" +
                        "dialect \"mvel\"\n" +
                        "when\n" +
                        "  $p : Person()\n" +
                        "then\n" +
                        "  Address a = new Address(\"somewhere\");\n" +
                        "  insert(a);\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Matteo", 47));
        ksession.fireAllRules();

        List<Address> results = getObjectsIntoList(ksession, Address.class);
        assertThat(results.size()).isEqualTo(1);
    }

    public static class TempDecl1 {}
    public static class TempDecl2 {}
    public static class TempDecl3 {}
    public static class TempDecl4 {}
    public static class TempDecl5 {}
    public static class TempDecl6 {}
    public static class TempDecl7 {}
    public static class TempDecl8 {}
    public static class TempDecl9 {}
    public static class TempDecl10 {}

    @Test
    public void testMVEL10declarations() {
        String str = "\n" +
                     "import " + TempDecl1.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl2.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl3.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl4.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl5.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl6.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl7.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl8.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl9.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl10.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $i1 : TempDecl1()\n" +
                     "  $i2 : TempDecl2()\n" +
                     "  $i3 : TempDecl3()\n" +
                     "  $i4 : TempDecl4()\n" +
                     "  $i5 : TempDecl5()\n" +
                     "  $i6 : TempDecl6()\n" +
                     "  $i7 : TempDecl7()\n" +
                     "  $i8 : TempDecl8()\n" +
                     "  $i9 : TempDecl9()\n" +
                     "  $i10 : TempDecl10()\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new TempDecl1());
        ksession.insert(new TempDecl2());
        ksession.insert(new TempDecl3());
        ksession.insert(new TempDecl4());
        ksession.insert(new TempDecl5());
        ksession.insert(new TempDecl6());
        ksession.insert(new TempDecl7());
        ksession.insert(new TempDecl8());
        ksession.insert(new TempDecl9());
        ksession.insert(new TempDecl10());
        ksession.fireAllRules();

        List<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void testMVEL10declarationsBis() {
        String str = "\n" +
                     "import " + TempDecl1.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl2.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl3.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl4.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl5.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl6.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl7.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl8.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl9.class.getCanonicalName() + ";\n" +
                     "import " + TempDecl10.class.getCanonicalName() + ";\n" +
                     "rule Rinit\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "then\n" +
                     "  insert( new TempDecl1() );\n" +
                     "  insert( new TempDecl2() );\n" +
                     "  insert( new TempDecl3() );\n" +
                     "  insert( new TempDecl4() );\n" +
                     "  insert( new TempDecl5() );\n" +
                     "  insert( new TempDecl6() );\n" +
                     "  insert( new TempDecl7() );\n" +
                     "  insert( new TempDecl8() );\n" +
                     "  insert( new TempDecl9() );\n" +
                     "  insert( new TempDecl10());\n" +
                     "end\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $i1 : TempDecl1()\n" +
                     "  $i2 : TempDecl2()\n" +
                     "  $i3 : TempDecl3()\n" +
                     "  $i4 : TempDecl4()\n" +
                     "  $i5 : TempDecl5()\n" +
                     "  $i6 : TempDecl6()\n" +
                     "  $i7 : TempDecl7()\n" +
                     "  $i8 : TempDecl8()\n" +
                     "  $i9 : TempDecl9()\n" +
                     "  $i10 : TempDecl10()\n" +
                     "then\n" +
                     "   insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.fireAllRules();

        List<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void testMvelFunctionWithClassArg() {
        final String drl =
                "package org.drools.compiler.integrationtests.drl; \n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "dialect \"mvel\"\n" +
                "global java.lang.StringBuilder value;\n" +
                "function String getFieldValue(Person bean) {" +
                "   return bean.getName();" +
                "}" +
                "\n" +
                "rule R1 \n" +
                "when \n" +
                "then \n" +
                "   insert( new Person( \"mario\" ) ); \n" +
                "end \n" +
                "\n" +
                "rule R2 \n" +
                "when \n" +
                "   $bean : Person( ) \n" +
                "then \n" +
                "   value.append( getFieldValue($bean) ); \n" +
                "end";

        KieSession ksession = getKieSession(drl);

        try {
            final StringBuilder sb = new StringBuilder();
            ksession.setGlobal( "value", sb );
            ksession.fireAllRules();

            assertThat(sb.toString()).isEqualTo("mario");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMvelFunctionWithDeclaredTypeArg() {
        final String drl =
                "package org.drools.compiler.integrationtests.drl; \n" +
                "dialect \"mvel\"\n" +
                "global java.lang.StringBuilder value;\n" +
                "function String getFieldValue(Bean bean) {" +
                "   return bean.getField();" +
                "}" +
                "declare Bean \n" +
                "   field : String \n" +
                "end \n" +
                "\n" +
                "rule R1 \n" +
                "when \n" +
                "then \n" +
                "   insert( new Bean( \"mario\" ) ); \n" +
                "end \n" +
                "\n" +
                "rule R2 \n" +
                "when \n" +
                "   $bean : Bean( ) \n" +
                "then \n" +
                "   value.append( getFieldValue($bean) ); \n" +
                "end";

        KieSession ksession = getKieSession(drl);

        try {
            final StringBuilder sb = new StringBuilder();
            ksession.setGlobal( "value", sb );
            ksession.fireAllRules();

            assertThat(sb.toString()).isEqualTo("mario");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMultiDrlWithSamePackageMvel() throws Exception {
        // DROOLS-3508
        String drl1 = "package org.pkg\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n"; // MVEL dialect defined at package level.

        String drl2 = "package org.pkg\n" +
                "rule R1\n" +
                "no-loop\n" +
                "when\n" +
                "   $p : Person( name == \"John\" )\n" +
                "then\n" +
                "   $p.age = 1;\n" +
                "   update($p);\n" +
                "end\n";

        KieSession ksession = getKieSession(drl1, drl2);

        Person john = new Person("John", 24);
        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getAge()).isEqualTo(1);
    }

    @Test
    public void testMVELNonExistingMethod() {
        // DROOLS-3559
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify($p) {likes = nonExistingMethod()};\n" +
                "end";

        Results results = createKieBuilder( drl ).getResults();
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void testBinaryOperationOnBigDecimal() throws Exception {
        // RHDM-1421
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    $p.money = $p.money + 50000;\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( 120000 ));
    }

    @Test
    public void testAdditionMultiplication() throws Exception {
        // DROOLS-6089
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    BigDecimal bd1 = 10;\n" +
                "    BigDecimal bd2 = 20;\n" +
                "    $p.money = $p.money + (bd1.multiply(bd2));" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( 70200 ));
    }

    @Test
    public void testBigDecimalModuloConsequence() {
        // DROOLS-5959
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "global java.util.List results;\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person($m : money)\n" +
                "then\n" +
                "    results.add($m % 70);\n" +
                "    BigDecimal moduloPromotedToBigDecimal = 12 % 10; "+
                "    results.add(moduloPromotedToBigDecimal);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<BigDecimal> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 71 ) );

        ksession.insert(john);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results).containsExactly(valueOf(1), valueOf(2));
    }

    @Test
    public void testBigDecimalModulo() {
        // DROOLS-5959
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List results;\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person($m : money % 2 == 0 )\n" +
                "then\n" +
                "    results.add($m);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<BigDecimal> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        Person mark = new Person("Mark", 40);
        mark.setMoney( new BigDecimal( 70001 ) );

        ksession.insert(john);
        ksession.insert(mark);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results.iterator().next()).isEqualTo(new BigDecimal( 70000 ));
    }

    @Test
    public void testBigDecimalModuloBetweenFields() {
        // DROOLS-5959
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List results;\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person($m : money % age == 20 )\n" +
                "then\n" +
                "    results.add($m);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<BigDecimal> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 90 ) );

        Person mark = new Person("Mark", 30);
        mark.setMoney( new BigDecimal( 80 ) );

        ksession.insert(john);
        ksession.insert(mark);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results.iterator().next()).isEqualTo(new BigDecimal( 80 ));
    }

    @Test
    public void testBigDecimalPatternWithString() {
        // DROOLS-6356 // DROOLS-6361
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "global java.util.List results;\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person($m : money == \"90\" )\n" +
                "then\n" +
                "    if($p.money == \"90\") {\n" +
                "       results.add($p);\n" +
                "    }\n" +
                "    $p.name = $m;\n"  +
                "    $p.name = $p.money;"  +
                "    $p.name = BigDecimal.ZERO;\n"  +
                "    $p.name = BigDecimal.valueOf(133);\n"  +
                "    $p.name = 144B;\n"  +
                "end";

        KieSession ksession = getKieSession(drl);

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 90 ) );

        Person mark = new Person("Mark", 30);
        mark.setMoney( new BigDecimal( 80 ) );

        ksession.insert(john);
        ksession.insert(mark);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results).containsOnly(john);
        assertThat(results.iterator().next().getName()).isEqualTo("144");
    }

    @Test
    public void testBigDecimalAccumulate() {
        // DROOLS-6366
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "global java.util.List results;\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    accumulate( Person($m : money); $maxMoney:max($m))\n" +
                "    $john : Person(money == $maxMoney)\n" +
                "then\n" +
                "    results.add($john);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 90 ) );

        Person mark = new Person("Mark", 30);
        mark.setMoney( new BigDecimal( 80 ) );

        ksession.insert(john);
        ksession.insert(mark);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results).containsExactly(john);
    }

    @Test
    public void testBigDecimalAccumulateWithFrom() {
        // DROOLS-6366
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "global java.util.List results;\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $maxMoney : BigDecimal() from accumulate( Person($m : money); max($m))\n" +
                "    $john : Person(money == $maxMoney)\n" +
                "then\n" +
                "    results.add($john);\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 90 ) );

        Person mark = new Person("Mark", 30);
        mark.setMoney( new BigDecimal( 80 ) );

        ksession.insert(john);
        ksession.insert(mark);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results).containsExactly(john);
    }

    @Test
    public void testCompoundOperatorBigDecimalConstant() throws Exception {
        // DROOLS-5894
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    BigDecimal result = 0B;" +
                "    result += 50000B;\n" + // 50000
                "    result -= 10000B;\n" + // 40000
                "    result /= 10B;\n" + // 4000
                "    result *= 10B;\n" + // 40000
                "    (result *= 10B);\n" + // 400000
                "    $p.money = result;" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( 400000 ));
    }

    @Test
    public void testCompoundOperatorBigDecimalConstantWithoutLiterals() {
        // DROOLS-5894 // DROOLS-5901
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    BigDecimal result = 0B;" +
                "    result += 50000;\n" + // 50000
                "    result -= 10000;\n" + // 40000
                "    result /= 10;\n" + // 4000
                "    result *= 10;\n" + // 40000
                "    result += result;\n" + // 80000
                "    result /= result;\n" + // 1
                "    result *= result;\n" + // 1
                "    result -= result;\n" + // 0
                "    int anotherVariable = 20;" +
                "    result += anotherVariable;\n" + // 20
                "    result /= anotherVariable;\n" + // 1
                "    result *= anotherVariable;\n" + // 20
                "    result -= anotherVariable;\n" + // 20
                "    $p.money = result;" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( 0 ));
    }

    @Test
    public void testArithmeticOperationsOnBigDecimal() {
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    BigDecimal operation = ($p.money + $p.otherBigDecimalField * 2) / 10;" +
                "    $p.money = operation;\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );
        john.setOtherBigDecimalField(new BigDecimal("10"));

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( 7002 ));
    }

    @Test
    public void testCompoundOperatorOnfield() throws Exception {

        // DROOLS-5895
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    $p.money += $p.money;\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( 140000 ));
    }

    @Test
    public void testModifyOnBigDecimal() {
        // DROOLS-5889
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "   list.add(\"before \" + $p + \", money = \" + $p.money);" +
                "   modify($p) {" +
                "       money = 30000;\n" +
                "   } " +
                "   list.add(\"after \" + $p + \", money = \" + $p.money);" +
                "end";

        KieSession ksession = getKieSession(drl);

        ArrayList<String> logMessages = new ArrayList<>();
        ksession.setGlobal("list", logMessages);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( 30000 ));
        assertThat(logMessages).containsExactly(
                "before John, money = 70000",
                "after John, money = 30000");
    }

    @Test
    public void testModifyOnBigDecimalWithLiteral() {
        // DROOLS-5891
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "   modify($p) {" +
                "       money = 1000.23B;\n" +
                "   } " +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        Person leonardo = new Person("Leonardo", 4);
        leonardo.setMoney( new BigDecimal( 500 ) );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( "1000.23" ));
        assertThat(leonardo.getMoney()).isEqualTo(new BigDecimal( 500 ));
    }

    @Test
    public void testBinaryOperationOnInteger() {
        // RHDM-1421
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    $p.salary = $p.salary + 50000;\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setSalary( 70000 );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat((int) john.getSalary()).isEqualTo(120000);
    }

    @Test
    public void testSetOnInteger() {
        // RHDM-1421
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    $p.salary = 50000;\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setSalary( 70000 );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat((int) john.getSalary()).isEqualTo(50000);
    }

    @Test
    public void testCollectSubtypeInConsequence() {
        // DROOLS-5887
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + ArrayList.class.getCanonicalName() + "\n" +
                "global java.util.List names;\n" +
                "dialect \"mvel\"\n" +
                "rule \"use subtype\"\n" +
                "when\n" +
                "    $people : ArrayList() from collect ( Person() )\n" +
                "then\n" +
                "    for (Person p : $people ) {\n" +
                "        names.add(p.getName());\n" +
                "    }\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<String> names = new ArrayList<>();
        ksession.setGlobal("names", names);

        Person mario = new Person("Mario", 46);
        Person luca = new Person("Luca", 36);
        Person leonardo = new Person("Leonardo", 3);

        Arrays.asList(mario, luca, leonardo).forEach(ksession::insert);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(names).containsExactlyInAnyOrder("Mario", "Luca", "Leonardo");
    }

    @Test
    public void testCollectSubtypeInConsequenceNested() {
        // DROOLS-5887
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + Address.class.getCanonicalName() + "\n" +
                "import " + ArrayList.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "global java.util.List names;\n" +
                "global java.util.Set  addresses;\n" +
                "rule \"use subtypes in nested fors\"\n" +
                "when\n" +
                "    $people : ArrayList() from collect ( Person() )\n" +
                "    $addresses : ArrayList() from collect ( Address() )\n" +
                "then\n" +
                "    for (Person p : $people ) {\n" +
                "        names.add(p.getName());\n" +
                "           for (Address a : $addresses ) {\n" +
                "               addresses.add(a.getCity());\n" +
                "       }\n" +
                "    }\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<String> names = new ArrayList<>();
        ksession.setGlobal("names", names);

        Set<String> addresses = new HashSet<>();
        ksession.setGlobal("addresses", addresses);


        Person mario = new Person("Mario", 46);
        Person luca = new Person("Luca", 36);
        Person leonardo = new Person("Leonardo", 3);

        Arrays.asList(mario, luca, leonardo).forEach(ksession::insert);

        Address a = new Address("Milan");
        ksession.insert(a);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(names).containsExactlyInAnyOrder("Mario", "Luca", "Leonardo");
        assertThat(addresses).contains("Milan");
    }

    @Test
    public void testSetOnMvel() {
        // RHDM-1550
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule \"use subtypes in nested fors\"\n" +
                "when\n" +
                "    $person: Person()\n" +
                "then\n" +
                "    $person.setNameAndAge( \"Mario\", 46\n" +
                ");\n" +
                "end";

        KieSession ksession = getKieSession(drl);

        Person mario = new Person();
        ksession.insert( mario );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(mario.getName()).isEqualTo("Mario");
        assertThat(mario.getAge()).isEqualTo(46);
    }

    @Test
    public void testCompoundOperator() throws Exception {
        // DROOLS-5894 // DROOLS-5901 // DROOLS-5897
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + BigDecimal.class.getCanonicalName() + "\n" +
                "dialect \"mvel\"\n" +
                "rule R\n" +
                "when\n" +
                "    $p : Person( age >= 26 )\n" +
                "then\n" +
                "    BigDecimal result = 0B;" +
                "    $p.money += 50000;\n" + // 50000
                "    $p.money -= 10000;\n" + // 40000
                "    $p.money /= 10;\n" + // 4000
                "    $p.money *= 10;\n" + // 40000
                "    $p.money += $p.money;\n" + // 80000
                "    $p.money /= $p.money;\n" + // 1
                "    $p.money *= $p.money;\n" + // 1
                "    $p.money -= $p.money;\n" + // 0
                "    BigDecimal anotherVar = 10B;" +
                "    $p.money += anotherVar;\n" + // 10
                "    $p.money /= anotherVar;\n" + // 1
                "    $p.money *= anotherVar;\n" + // 1
                "    $p.money -= anotherVar;\n" + // 0
                "    int intVar = 20;" +
                "    $p.money += intVar;\n" + // 20
                "    $p.money /= intVar;\n" + // 1
                "    $p.money *= intVar;\n" + // 1
                "    $p.money -= intVar;\n" + // 0
                "end";

        KieSession ksession = getKieSession(drl);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 70000 ) );

        ksession.insert(john);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(john.getMoney()).isEqualTo(new BigDecimal( 0 ));
    }

    @Test
    public void testKcontext() {
        String str =
                "global java.util.List result;" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  Integer()\n" +
                     "then\n" +
                     "  result.add(kcontext.getRule().getName());\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(47);
        ksession.fireAllRules();

        assertThat(result.contains("R")).isTrue();
    }

    @Test
    public void testLineBreakAtTheEndOfStatementWithoutSemicolon() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "rule R\n" +
                           "dialect \"mvel\"\n" +
                           "when\n" +
                           "  Person(name == \"Mario\")\n" +
                           "then\n" +
                           "  Person p2 = new Person(\"John\");\n" +
                           "  p2.age = 30\n" + // a line break at the end of the statement without a semicolon
                           "  insert(p2);\n" +
                           "end";

        KieSession ksession = getKieSession(str);

        Person p = new Person("Mario", 40);
        ksession.insert(p);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

    @Test
    public void testSetNullInModify() {
        // RHDM-1713
        String str =
                "dialect \"mvel\"\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify($p) { name = null }\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person( name == null )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person me = new Person( "Mario", 47 );
        ksession.insert( me );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testSetSubclassInModify() {
        // RHDM-1713
        String str =
                "dialect \"mvel\"\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + InternationalAddress.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify($p) { address = new InternationalAddress(\"home\") }\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person( address != null )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person me = new Person( "Mario", 47 );
        ksession.insert( me );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testForEachAccessor() {
        // DROOLS-6298
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "global java.util.List results;" +
                "dialect \"mvel\"\n" +
                "rule \"rule_for_each\"\n" +
                "    when\n" +
                "        $p : Person( )\n" +
                "    then\n" +
                "        for(Address a: $p.addresses){\n" +
                        "  results.add(a.city);\n" +
                        "}\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ArrayList<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person me = new Person( "Mario", 47 );
        Address address = new Address("Address");
        me.addAddress(address);

        ksession.insert( me);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(results).containsOnly("Address");
    }

    @Test
    public void testBigDecimalPromotionUsedAsArgument() {
        // DROOLS-6362
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "import static " + Person.class.getCanonicalName() + ".isEven;" +
                "import static " + Person.class.getCanonicalName() + ".isEvenShort;" +
                "import static " + Person.class.getCanonicalName() + ".isEvenDouble;" +
                "import static " + Person.class.getCanonicalName() + ".isEvenFloat;" +
                "global java.util.List results;" +
                "dialect \"mvel\"\n" +
                "rule \"isEven\"\n" +
                "    when\n" +
                "        $p : Person(" +
                                    "isEven($p.money), " +
                                    "isEvenShort($p.money), " +
                                    "isEvenDouble($p.money), " +
                                    "isEvenFloat($p.money), " +
                                    "$m : money)\n" +
                "    then\n" +
                "       if (" +
                        "   $p.isEven($p.money) " +
                        "   && $p.isEven($m) " +
                        "   && isEven($p.money) " +
                        "   && isEven($m)" +
                        "   && $p.isEvenShort($p.money) " +
                        "   && $p.isEvenShort($m) " +
                        "   && isEvenShort($p.money) " +
                        "   && isEvenShort($m)" +
                        "   && $p.isEvenDouble($p.money) " +
                        "   && $p.isEvenDouble($m) " +
                        "   && isEvenDouble($p.money) " +
                        "   && isEvenDouble($m)" +
                        "   && $p.isEvenFloat($p.money) " +
                        "   && $p.isEvenFloat($m) " +
                        "   && isEvenFloat($p.money) " +
                        "   && isEvenFloat($m)" +
                        ") {\n" +
                        "" +
                        "  results.add($p);\n" +
                        "}\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ArrayList<Person> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John", 30);
        john.setMoney( new BigDecimal( 3 ) );

        Person leonardo = new Person("Leonardo", 4);
        leonardo.setMoney( new BigDecimal( 4 ) );

        ksession.insert(john);
        ksession.insert(leonardo);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(results).containsOnly(leonardo);
    }


    public static BigDecimal bigDecimalFunc( BigDecimal bd){
        return new BigDecimal(bd.toString());
    }

    @Test
    public void testBigDecimalPromotionWithExternalFunction() {
        // DROOLS-6410
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "import " + BigDecimal.class.getCanonicalName() + ";" +
                "import static " + MvelDialectTest.class.getCanonicalName() + ".bigDecimalFunc;" +
                "import static " + Person.class.getCanonicalName() + ".identityBigDecimal;" +
                "global java.util.List results;" +
                "dialect \"mvel\"\n" +
                "rule \"bigDecimalFunc\"\n" +
                "    when\n" +
                "        $p : Person($bd : (bigDecimalFunc(money) * 100 + 12), " +
                        "            $bd2 : (identityBigDecimal(money) * 100 + 12))\n" + // 1012
                "    then\n" +
                        "BigDecimal resultAssigned = (bigDecimalFunc($p.money) * 100 + 12);\n" + // 1012
                        "results.add(resultAssigned);\n" + // 101212
                        "results.add($bd2);\n" + // 101212
                        "results.add((identityBigDecimal($p.money) * 100 + 12));\n" + // 101212
                        "results.add(bigDecimalFunc($bd) * 100 + 12);\n" + // 101212
                "end\n";

        KieSession ksession = getKieSession( str );

        ArrayList<BigDecimal> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person leonardo = new Person("Leonardo", 4);
        leonardo.setMoney( new BigDecimal( 10 ) );

        ksession.insert(leonardo);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(results).containsExactly(valueOf(1012), valueOf(1012), valueOf(1012), valueOf(101212));
    }

    @Test
    public void testBigDecimalPromotionUsingDefinedFunctionAndDeclaredType() {
        // DROOLS-6362
        String str = "package com.sample\n" +
                "import " + Person.class.getName() + ";\n" +
                "import " + BigDecimal.class.getName() + ";\n" +
                "global java.util.List results;" +
                "declare POJOPerson\n" +
                "    name : String\n" +
                "    age : int\n" +
                "    salary : BigDecimal\n" +
                "end\n" +
                "function int myFunction(int value) {\n" +
                "  if (value == 10) {\n" +
                "    return 1;\n" +
                "  }\n" +
                "  return 0;\n" +
                "}\n" +
                "dialect \"mvel\"\n" +
                "rule create\n" +
                "    when\n" +
                "        $p: Person()\n" +
                "    then\n" +
                "       insert(new POJOPerson($p.name, $p.age, $p.money))\n" +
                "end\n" +
                "rule R1\n" +
                "    when\n" +
                "        $p: POJOPerson(myFunction(salary) == 1)\n" +
                "    then\n" +
                "       if (myFunction($p.salary) == 1) {" +
                "           results.add($p.name);\n" +
                        "}\n" +
                "end\n";


        KieSession ksession = getKieSession( str );

        ArrayList<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person john = new Person("John", 10).setMoney(valueOf(10));
        ksession.insert(john);

        Person leonardo = new Person("Leonardo", 4).setMoney(valueOf(4));
        ksession.insert(leonardo);

        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(3);
        assertThat(results).containsExactly("John");
    }

    @Test
    public void testMVELMapRHSGetAndAssign() {
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "dialect \"mvel\"\n" +
                     "global java.util.List result;\n" +
                     "rule \"rule_mt_1a\"\n" +
                     "    when\n" +
                     "        $p : Person($age : age)\n" +
                     "    then\n" +
                     "        Integer i = $p.items[$age];\n" +
                     "        result.add(i);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Integer> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person person = new Person("John", 20);
        person.getItems().put(20, 100);
        ksession.insert(person);
        ksession.fireAllRules();
        assertThat(result).containsExactly(100);
    }

    @Test
    public void testRHSMapGetAsParam() {
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "dialect \"mvel\"\n" +
                     "global java.util.List result;\n" +
                     "rule R1\n" +
                     "    when\n" +
                     "        $p : Person($name : name)\n" +
                     "    then\n" +
                     "        result.add($p.itemsString[$name]);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person person = new Person("John");
        person.getItemsString().put("John", "OK");
        ksession.insert(person);
        ksession.fireAllRules();
        assertThat(result).containsExactly("OK");
    }

    @Test
    public void testRHSMapNestedProperty() {
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "dialect \"mvel\"\n" +
                     "global java.util.List result;\n" +
                     "rule R1\n" +
                     "    when\n" +
                     "        $p : Person( $name: name )\n" +
                     "    then\n" +
                     "        result.add($p.childrenMap[$name].age);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Integer> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person parent = new Person("John", 30);
        Person child = new Person("John", 5);
        parent.getChildrenMap().put("John", child);

        ksession.insert(parent);
        ksession.fireAllRules();
        assertThat(result).containsExactly(5);
    }

    @Test
    public void testRHSListNestedProperty() {
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "dialect \"mvel\"\n" +
                     "global java.util.List result;\n" +
                     "rule R1\n" +
                     "    when\n" +
                     "        $p : Person( $age: age )\n" +
                     "    then\n" +
                     "        result.add($p.addresses[$age].city);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person john = new Person("John", 0);
        Address address = new Address("London");
        john.getAddresses().add(address);

        ksession.insert(john);
        ksession.fireAllRules();
        assertThat(result).containsExactly("London");
    }

    @Test
    public void testRHSMapMethod() {
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "dialect \"mvel\"\n" +
                     "global java.util.List result;\n" +
                     "rule R1\n" +
                     "    when\n" +
                     "        $p : Person( $name: name )\n" +
                     "    then\n" +
                     "        result.add($p.itemsString.size);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Integer> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Person person = new Person("John");
        person.getItemsString().put("John", "OK");
        ksession.insert(person);
        ksession.fireAllRules();
        assertThat(result).containsExactly(1);
    }

    @Test
    public void testMVELBigIntegerLiteralRHS() {
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  $p.setAgeInSeconds(10000I);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person p = new Person();
        ksession.insert(p);
        ksession.fireAllRules();

        assertThat(p.getAgeInSeconds().equals(new BigInteger("10000"))).isTrue();
    }

    @Test
    public void testMVELBigDecimalLiteralRHS() {
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  $p.setMoney(10000B);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person p = new Person();
        ksession.insert(p);
        ksession.fireAllRules();

        assertThat(p.getMoney().equals(new BigDecimal("10000"))).isTrue();
    }

    @Test
    public void testMVELBigIntegerLiteralLHS() {
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person(ageInSeconds == 10000I)\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person p = new Person();
        p.setAgeInSeconds(new BigInteger("10000"));
        ksession.insert(p);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

    @Test
    public void testMVELModifyPropMethodCall() {
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  modify($p) {\n" +
                "    age = 20,\n" +
                "    addresses.clear();\n" +
                "  }\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person p = new Person("John");
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address("London"));
        p.setAddresses(addresses);
        ksession.insert(p);
        int fired = ksession.fireAllRules();

        assertThat(p.getAge()).isEqualTo(20);
        assertThat(p.getAddresses().size()).isEqualTo(0);
        assertThat(fired).isEqualTo(1);
    }

    @Test
    public void assign_primitiveBooleanProperty() {
        // DROOLS-7250
        String str = "package com.example.reproducer\n" +
                     "import " + ValueHolder.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $holder : ValueHolder()\n" +
                     "then\n" +
                     "  $holder.primitiveBooleanValue = true;\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ValueHolder holder = new ValueHolder();
        holder.setPrimitiveBooleanValue(false);
        ksession.insert(holder);
        ksession.fireAllRules();

        assertThat(holder.isPrimitiveBooleanValue()).isTrue();
    }

    @Test
    public void assign_wrapperBooleanProperty() {
        // DROOLS-7250
        String str = "package com.example.reproducer\n" +
                     "import " + ValueHolder.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $holder : ValueHolder()\n" +
                     "then\n" +
                     "  $holder.wrapperBooleanValue = true;\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ValueHolder holder = new ValueHolder();
        holder.setWrapperBooleanValue(false);
        ksession.insert(holder);
        ksession.fireAllRules();

        assertThat(holder.getWrapperBooleanValue()).isTrue();
    }

    public void assign_nestedProperty() {
        // DROOLS-7195
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  $p.address.city = \"Tokyo\";\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person p = new Person("John");
        p.setAddress(new Address("London"));
        ksession.insert(p);
        ksession.fireAllRules();

        assertThat(p.getAddress().getCity()).isEqualTo("Tokyo");
    }

    @Test
    public void assign_nestedPropertyInModify() {
        // DROOLS-7195
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person(name == \"John\")\n" +
                "then\n" +
                "  modify($p) {" +
                "    address.city = \"Tokyo\";\n" +
                "  }\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person p = new Person("John");
        p.setAddress(new Address("London"));
        ksession.insert(p);
        ksession.fireAllRules();

        assertThat(p.getAddress().getCity()).isEqualTo("Tokyo");
    }

    @Test
    public void setter_nestedPropertyInModify() {
        // DROOLS-7195
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person(name == \"John\")\n" +
                "then\n" +
                "  modify($p) {" +
                "    address.setCity(\"Tokyo\");\n" +
                "  }\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person p = new Person("John");
        p.setAddress(new Address("London"));
        ksession.insert(p);
        ksession.fireAllRules();

        assertThat(p.getAddress().getCity()).isEqualTo("Tokyo");
    }

    @Test
    public void assign_deepNestedPropertyInModify() {
        // DROOLS-7195
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person(name == \"John\")\n" +
                "then\n" +
                "  modify($p) {" +
                "    address.visitorCounter.value = 1;\n" +
                "  }\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person person = new Person("John");
        Address address = new Address("London");
        Counter counter = new Counter();
        counter.setValue(0);
        address.setVisitorCounter(counter);
        person.setAddress(address);
        ksession.insert(person);
        ksession.fireAllRules();

        assertThat(person.getAddress().getVisitorCounter().getValue()).isEqualTo(1);
    }

    @Test
    public void setter_deepNestedPropertyInModify() {
        // DROOLS-7195
        String str = "package com.example.reproducer\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  $p : Person(name == \"John\")\n" +
                "then\n" +
                "  modify($p) {" +
                "    address.visitorCounter.setValue(1);\n" +
                "  }\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Person person = new Person("John");
        Address address = new Address("London");
        Counter counter = new Counter();
        counter.setValue(0);
        address.setVisitorCounter(counter);
        person.setAddress(address);
        ksession.insert(person);
        ksession.fireAllRules();

        assertThat(person.getAddress().getVisitorCounter().getValue()).isEqualTo(1);
    }

    @Test
    public void drools_workingMemory_setGlobal() {
        // DROOLS-7338
        String str = "package com.example.reproducer\n" +
                     "import " + Logger.class.getCanonicalName() + ";\n" +
                     "global Logger logger;\n" +
                     "rule R\n" +
                     "  dialect \"mvel\"\n" +
                     "  when\n" +
                     "  then\n" +
                     "    drools.workingMemory.setGlobal(\"logger\", org.slf4j.LoggerFactory.getLogger(getClass()));\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.fireAllRules();

        Object logger = ksession.getGlobal("logger");
        assertThat(logger).isInstanceOf(Logger.class);
    }

    @Test
    public void drools_fieldAccess() {
        String str = "package com.example.reproducer\n" +
                     "global java.util.Map results;\n" +
                     "rule R\n" +
                     "  dialect \"mvel\"\n" +
                     "  when\n" +
                     "  then\n" +
                     "    results.put(\"workingMemory\", drools.workingMemory);\n" +
                     "    results.put(\"rule\", drools.rule);\n" +
                     "    results.put(\"match\", drools.match);\n" +
                     "    results.put(\"tuple\", drools.tuple);\n" +
                     "    results.put(\"knowledgeRuntime\", drools.knowledgeRuntime);\n" +
                     "    results.put(\"kieRuntime\", drools.kieRuntime);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        Map<String, Object> results = new HashMap<>();
        ksession.setGlobal("results", results);

        ksession.fireAllRules();

        assertThat(results.get("workingMemory")).isInstanceOf(WorkingMemory.class);
        assertThat(results.get("rule")).isInstanceOf(Rule.class);
        assertThat(results.get("match")).isInstanceOf(Match.class);
        assertThat(results.get("tuple")).isInstanceOf(Tuple.class);
        assertThat(results.get("knowledgeRuntime")).isInstanceOf(KieRuntime.class);
        assertThat(results.get("kieRuntime")).isInstanceOf(KieRuntime.class);
    }

    @Test
    public void integerToBigDecimalBindVariableCoercion_shouldNotCoerceToInteger() {
        // DROOLS-7540
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "global java.util.List results;\n" +
                     "rule R\n" +
                     "  dialect \"mvel\"\n" +
                     "  when\n" +
                     "    Person($money : money)\n" +
                     "  then\n" +
                     "    $money = 5;\n" +
                     "    results.add($money);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Object> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person person = new Person("John");
        person.setMoney(new BigDecimal("0"));
        ksession.insert(person);
        ksession.fireAllRules();

        assertThat(results.get(0)).isInstanceOf(BigDecimal.class);
        assertThat(results).contains(new BigDecimal("5"));
    }

    @Test
    public void integerToBigDecimalVariableCoercionTwice_shouldNotCoerceToInteger() {
        // DROOLS-7540
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "global java.util.List results;\n" +
                     "rule R\n" +
                     "  dialect \"mvel\"\n" +
                     "  when\n" +
                     "    Person()\n" +
                     "  then\n" +
                     "    BigDecimal $var1 = 5;\n" +
                     "    $var1 = 5;\n" + // This causes the issue
                     "    results.add($var1);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Object> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person person = new Person("John");
        person.setMoney(new BigDecimal("0"));
        ksession.insert(person);
        ksession.fireAllRules();

        assertThat(results.get(0)).isInstanceOf(BigDecimal.class);
        assertThat(results).contains(new BigDecimal("5"));
    }

    @Test
    public void integerToBigDecimalBindVariableCoercionAndAddition_shouldNotThrowClassCastException() {
        // DROOLS-7540
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "global java.util.List results;\n" +
                     "rule R\n" +
                     "  dialect \"mvel\"\n" +
                     "  when\n" +
                     "    Person($money : money, $otherBigDecimalField : otherBigDecimalField)\n" +
                     "  then\n" +
                     "    $money = 5;\n" +
                     "    $total = $money + $otherBigDecimalField;\n" +
                     "    results.add($total);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Object> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person person = new Person("John");
        person.setMoney(new BigDecimal("0"));
        person.setOtherBigDecimalField(new BigDecimal("10"));
        ksession.insert(person);
        ksession.fireAllRules();

        assertThat(results).contains(new BigDecimal("15"));
    }

    @Test
    public void integerToBigDecimalVaribleSetFromBindVariableCoercionAndAddition_shouldNotThrowClassCastException() {
        // DROOLS-7540
        String str = "package com.example.reproducer\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "global java.util.List results;\n" +
                     "rule R\n" +
                     "  dialect \"mvel\"\n" +
                     "  when\n" +
                     "    Person($money : money, $otherBigDecimalField : otherBigDecimalField)\n" +
                     "  then\n" +
                     "    BigDecimal $var1 = $money;\n" +
                     "    BigDecimal $var2 = $otherBigDecimalField;\n" +
                     "    $var1 = 5;\n" +
                     "    BigDecimal $total = $var1 + $var2;\n" +
                     "    results.add($total);\n" +
                     "end";

        KieSession ksession = getKieSession(str);
        List<Object> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person person = new Person("John");
        person.setMoney(new BigDecimal("0"));
        person.setOtherBigDecimalField(new BigDecimal("10"));
        ksession.insert(person);
        ksession.fireAllRules();

        assertThat(results).contains(new BigDecimal("15"));
    }
}
