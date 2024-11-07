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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.drools.base.base.ClassObjectType;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.drools.model.codegen.execmodel.domain.StockTick;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeSharingTest extends BaseModelTest {

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareAlpha(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : java.util.Set()\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "  $p2 : Person(name != \"Edson\", age > $p1.age)\n" +
                "  $p3 : Person(name != \"Edson\", age > $p1.age, this != $p2)\n" +
                "then\n" +
                "  $r.add($p2);\n" +
                "  $r.add($p3);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Set result = new HashSet<>();
        ksession.insert( result );

        Person mark = new Person( "Mark", 37 );
        Person edson = new Person( "Edson", 35 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( edson );
        ksession.insert( mario );
        ksession.fireAllRules();

        assertThat(result.contains(mark)).isTrue();
        assertThat(result.contains(mario)).isTrue();

        // Alpha node "name != Edson" should be shared between 3rd and 4th pattern.
        // therefore alpha nodes should be a total of 2: name == Edson, name != Edson
        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count()).isEqualTo(2);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareAlphaInDifferentRules(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareAlphaInDifferentPackages(RUN_TYPE runType) {
        String str1 =
                "package org.drools.a\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "then\n" +
                "end\n";
        String str2 =
                "package org.drools.b\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R2 when\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType,  str1, str2);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareBetaWithConstraintReordering(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $s : String()\n" +
                "  $p : Person(name != $s, age == $s.length)\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $s : String()\n" +
                "  $p : Person(name != $s, age == $s.length)\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(BetaNode.class::isInstance).count()).isEqualTo(1);

        EntryPointNode epn = ((InternalKnowledgeBase) ksession.getKieBase()).getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( Person.class ) );
        assertThat(otn.getSinks().length).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testTrimmedConstraint(RUN_TYPE runType) {

        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "   Person( name == \"Mark\")\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $p: Person( name==\"Mark\" )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, str);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testOrWithTrimmedConstraint(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R1 when\n" +
                "   Person( $name: name == \"Mark\", $age: age ) or\n" +
                "   Person( $name: name == \"Mario\", $age : age )\n" +
                "then\n" +
                "  list.add( $name + \" is \" + $age);\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $p: Person( name==\"Mark\", $age: age ) or\n" +
                "   $p: Person( name==\"Mario\", $age : age )\n" +
                "then\n" +
                "  list.add( $p + \" has \" + $age + \" years\");\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, str);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count()).isEqualTo(2);

        List<String> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(4);
        assertThat(results.contains("Mark is 37")).isTrue();
        assertThat(results.contains("Mark has 37 years")).isTrue();
        assertThat(results.contains("Mario is 40")).isTrue();
        assertThat(results.contains("Mario has 40 years")).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareAlphaHashable(RUN_TYPE runType) {
        String str =
                "import " + Factor.class.getCanonicalName() + ";\n" +
                     "rule R1 when\n" +
                     "    Factor( factorAmt == 10.00 )\n" +
                     "then end\n" +
                     "rule R2 when\n" +
                     "    Factor( factorAmt == 10.0 )\n" +
                     "then end\n";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Factor(10.0));
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareAlphaRangeIndexable(RUN_TYPE runType) {
        String str =
                "import " + Factor.class.getCanonicalName() + ";\n" +
                     "rule R1 when\n" +
                     "    Factor( factorAmt > 10.00 )\n" +
                     "then end\n" +
                     "rule R2 when\n" +
                     "    Factor( factorAmt > 10.0 )\n" +
                     "then end\n";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Factor(25.0));
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AlphaNode.class::isInstance).count()).isEqualTo(1);
    }

    public static class Factor {

        private final double factorAmt;

        public Factor(double factorAmt) {
            this.factorAmt = factorAmt;
        }

        public double getFactorAmt() {
            return factorAmt;
        }
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareEval(RUN_TYPE runType) {
        final String str = "package myPkg;\n" +
                           "rule R1 when\n" +
                           "  i : Integer()\n" +
                           "  eval(i > 100)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R2 when\n" +
                           "  i : Integer()\n" +
                           "  eval(i > 100)\n" +
                           "then\n" +
                           "end\n" +
                           "";

        KieSession ksession = getKieSession(runType, str);
        KieBase kbase = ksession.getKieBase();

        EntryPointNode epn = ((InternalKnowledgeBase) kbase).getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().get(new ClassObjectType(Integer.class));
        LeftInputAdapterNode lian = (LeftInputAdapterNode) otn.getSinks()[0];
        assertThat(lian.getSinks().length).isEqualTo(1);
    }

    public void testShareFrom(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                     "import " + Address.class.getCanonicalName() + ";\n" +
                     "global java.util.List list\n" +
                     "rule R1 when\n" +
                     "    $p : Person()\n" +
                     "    $a : Address(city == \"Tokyo\") from $p.addresses\n" +
                     "then\n" +
                     "  list.add($a);\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "    $p : Person()\n" +
                     "    $a : Address(city == \"Tokyo\") from $p.addresses\n" +
                     "then\n" +
                     "  list.add($a);\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, str);

        List<Address> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person p = new Person("John");
        p.getAddresses().add(new Address("ABC", 1, "Tokyo"));
        p.getAddresses().add(new Address("DEF", 2, "Tokyo"));
        p.getAddresses().add(new Address("GHI", 3, "Osaka"));

        ksession.insert(p);
        assertThat(ksession.fireAllRules()).isEqualTo(4);
        assertThat(list.size()).isEqualTo(4);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(FromNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareAccumulate(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Result.class.getCanonicalName() + ";" +
                     "rule X1 when\n" +
                     "  accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
                     "                $sum : sum($p.getAge())  \n" +
                     "              )                          \n" +
                     "then\n" +
                     "  insert(new Result($sum));\n" +
                     "end\n" +
                     "rule X2 when\n" +
                     "  accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
                     "                $sum : sum($p.getAge())  \n" +
                     "              )                          \n" +
                     "then\n" +
                     "  insert(new Result($sum));\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        assertThat(ksession.fireAllRules()).isEqualTo(2);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AccumulateNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareFromAccumulate(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Result.class.getCanonicalName() + ";" +
                     "rule X1 when\n" +
                     "  $sum : Number( intValue() > 0 )from accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
                     "                sum($p.getAge())  \n" +
                     "              )\n" +
                     "then\n" +
                     "  insert(new Result($sum));\n" +
                     "end\n" +
                     "rule X2 when\n" +
                     "  $sum : Number( intValue() > 0 )from accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
                     "                sum($p.getAge())  \n" +
                     "              )\n" +
                     "then\n" +
                     "  insert(new Result($sum));\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        assertThat(ksession.fireAllRules()).isEqualTo(2);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(AccumulateNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareExists(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Result.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  exists Person( name.length == 5 )\n" +
                     "then\n" +
                     "  insert(new Result(\"ok\"));\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  exists Person( name.length == 5 )\n" +
                     "then\n" +
                     "  insert(new Result(\"ok\"));\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Person mario = new Person("Mario", 40);

        ksession.insert(mario);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.iterator().next().getValue()).isEqualTo("ok");

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(ExistsNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareNot(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Result.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  not( Person( name.length == 4 ) )\n" +
                     "then\n" +
                     "  insert(new Result(\"ok\"));\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  not( Person( name.length == 4 ) )\n" +
                     "then\n" +
                     "  insert(new Result(\"ok\"));\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Person mario = new Person("Mario", 40);

        ksession.insert(mario);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.iterator().next().getValue()).isEqualTo("ok");

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(NotNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareCombinedConstraintAnd(RUN_TYPE runType) throws Exception {
        // DROOLS-6330
        // Note: if DROOLS-6329 is resolved, this test may not produce CombinedConstraint
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "    $a : StockTick( company == \"DROO\" )\n" +
                     "    $b : StockTick( company == \"ACME\" && this after[5s,8s] $a )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "    $a : StockTick( company == \"DROO\" )\n" +
                     "    $b : StockTick( company == \"ACME\" && this after[5s,8s] $a )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, CepTest.getCepKieModuleModel(), str);

        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));

        assertThat(ksession.fireAllRules()).isEqualTo(2);

        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));

        assertThat(ksession.fireAllRules()).isEqualTo(0);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(JoinNode.class::isInstance).count()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testShareCombinedConstraintOr(RUN_TYPE runType) throws Exception {
        // DROOLS-6330
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "    $a : StockTick( company == \"DROO\" )\n" +
                     "    $b : StockTick( company == \"XXXX\" || this after[5s,8s] $a )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "    $a : StockTick( company == \"DROO\" )\n" +
                     "    $b : StockTick( company == \"XXXX\" || this after[5s,8s] $a )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, CepTest.getCepKieModuleModel(), str);

        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));

        assertThat(ksession.fireAllRules()).isEqualTo(2);

        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));

        assertThat(ksession.fireAllRules()).isEqualTo(0);

        assertThat(ReteDumper.collectNodes(ksession).stream().filter(JoinNode.class::isInstance).count()).isEqualTo(1);
    }
}
