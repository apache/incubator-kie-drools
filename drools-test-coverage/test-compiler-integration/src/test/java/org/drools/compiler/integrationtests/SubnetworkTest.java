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
package org.drools.compiler.integrationtests;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.definition.type.Role;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class SubnetworkTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @Role(Role.Type.EVENT)
    public static class A {

    }

    @Role(Role.Type.EVENT)
    public static class B {

    }

    @Role(Role.Type.EVENT)
    public static class C {

    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testRightStagingOnSharedSubnetwork(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // RHBRMS-2624
        final String drl =
                "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                        "rule R1y when\n" +
                        "    AtomicInteger() \n" +
                        "    Number() from accumulate ( AtomicInteger( ) and $s : String( ) ; count($s) )" +
                        "    Long()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule R1x when\n" +
                        "    AtomicInteger() \n" +
                        "    Number() from accumulate ( AtomicInteger( ) and $s : String( ) ; count($s) )\n" +
                        "then\n" +
                        "end\n" +
                        "" +
                        "rule R2 when\n" +
                        "    $i : AtomicInteger( get() < 3 )\n" +
                        "then\n" +
                        "    $i.incrementAndGet();" +
                        "    update($i);" +
                        "end\n" +
                        "\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new AtomicInteger(0));
            kieSession.insert("test");

            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testUpdateOnSharedSubnetwork(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-1360
        final String drl =
                "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R1y when\n" +
                        "    AtomicInteger() \n" +
                        "    Number() from accumulate ( AtomicInteger( ) and $s : String( ) ; count($s) )" +
                        "    eval(false)\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule R2 when\n" +
                        "    $i : AtomicInteger( get() < 3 )\n" +
                        "then\n" +
                        "    $i.incrementAndGet();" +
                        "    insert(\"test\" + $i.get());" +
                        "    update($i);" +
                        "end\n" +
                        "\n" +
                        "rule R1x when\n" +
                        "    AtomicInteger() \n" +
                        "    $c : Number() from accumulate ( AtomicInteger( ) and $s : String( ) ; count($s) )\n" +
                        "    eval(true)\n" +
                        "then\n" +
                        "    list.add($c);" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<Number> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            kieSession.insert(new AtomicInteger(0));
            kieSession.insert("test");

            kieSession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0).intValue()).isEqualTo(4);
        } finally {
            kieSession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testSubNeworkNotRemoveRightRemove(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
              "import " + Dimension.class.getCanonicalName() + ";\n" +
              "global java.util.List list;\n" +
              "rule xR1y when\n" +
              "    String(this == \"go\") \n" +
              "    not(x : Dimension(x_height : height) and\n" +
              "        y : Dimension(this!=x, height!=x_height))" +
              "    eval(true)\n" +
              "then\n" +
              "    list.add(\"matched\"); \n" +
              "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<Number> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            kieSession.insert("go");
            FactHandle fh1 = kieSession.insert(new Dimension(100, 100));
            FactHandle fh2 = kieSession.insert(new Dimension(100, 200));
            kieSession.fireAllRules();

            kieSession.update(fh2, new Dimension(100, 100));
            kieSession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
        } finally {
            kieSession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testSubNeworkNotRemoveLeftRemove(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
              "import " + Dimension.class.getCanonicalName() + ";\n" +
              "global java.util.List list;\n" +
              "rule xR1y when\n" +
              "    String(this == \"go\") \n" +
              "    not(x : Dimension(x_height : height) and\n" +
              "        y : Dimension(this!=x, height!=x_height))" +
              "    eval(true)\n" +
              "then\n" +
              "    list.add(\"matched\"); \n" +
              "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<Number> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            FactHandle fhg = kieSession.insert("go");
            FactHandle fh1 = kieSession.insert(new Dimension(100, 100));
            FactHandle fh2 = kieSession.insert(new Dimension(100, 200));
            kieSession.fireAllRules();
            assertThat(list.size()).isEqualTo(0);
            list.clear();;

            kieSession.update(fh2, new Dimension(100, 100));
            kieSession.delete(fhg);
            kieSession.fireAllRules();
            assertThat(list.size()).isEqualTo(0);


        } finally {
            kieSession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testSubNeworkQueryTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
              "import " + Dimension.class.getCanonicalName() + ";\n" +
              "global java.util.List list;\n" +
              "query q1(String s)\n" +
              "    not(x : Dimension(x_height : height) and\n" +
              "        y : Dimension(this!=x, height!=x_height))" +
              "    eval(true)\n" +
              "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<Number> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            FactHandle fh1 = kieSession.insert(new Dimension(100, 100));
            FactHandle fh2 = kieSession.insert(new Dimension(100, 200));
            kieSession.fireAllRules();

            QueryResults results = kieSession.getQueryResults("q1", "go");
            assertThat(results.size()).isEqualTo(0);

            kieSession.update(fh2, new Dimension(100, 100));
            kieSession.fireAllRules();

            results = kieSession.getQueryResults("q1", "go");
            assertThat(results.size()).isEqualTo(1);

            kieSession.update(fh1, new Dimension(100, 200));
            kieSession.fireAllRules();

            results = kieSession.getQueryResults("q1", "go");
            assertThat(results.size()).isEqualTo(0);

            kieSession.update(fh1, new Dimension(100, 100));
            kieSession.fireAllRules();

            results = kieSession.getQueryResults("q1", "go");
            assertThat(results.size()).isEqualTo(1);

        } finally {
            kieSession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testSubNetworks(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources("subnetwork-test", kieBaseTestConfiguration,
                                                                             "org/drools/compiler/integrationtests/test_SubNetworks.drl");
        final KieSession session = kieBase.newKieSession();
        session.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testSubnetworkSharingWith2SinksFromLia(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-1656
        final String drl =
                "import " + X.class.getCanonicalName() + "\n" +
                        "import " + Y.class.getCanonicalName() + "\n" +
                        "global java.util.List list" +
                        "\n" +
                        "rule R1 agenda-group \"G2\" when\n" +
                        "    Number( intValue == 0 ) from accumulate (\n" +
                        "        X( $id : id )\n" +
                        "        and $y : Y( parentId == $id )\n" +
                        "    ;count($y))\n" +
                        "then\n" +
                        "    list.add(\"R1\");\n" +
                        "end\n" +
                        "\n" +
                        "rule R2 agenda-group \"G1\" when\n" +
                        "    Number( intValue < 1 ) from accumulate (\n" +
                        "        X( $id : id )\n" +
                        "        and $y : Y( parentId == $id )\n" +
                        "    ;count($y))\n" +
                        "then\n" +
                        "    list.add(\"R2\");\n" +
                        "end\n" +
                        "\n" +
                        "rule R3 agenda-group \"G1\" no-loop when\n" +
                        "    $x : X( $id : id )\n" +
                        "then\n" +
                        "    modify($x) { setId($id + 1) };\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-cep-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            kieSession.insert(new X(1));
            kieSession.insert(new Y(1));

            // DROOLS-2258
            kieSession.insert(new X(3));
            kieSession.insert(new Y(3));

            final Agenda agenda = kieSession.getAgenda();
            agenda.getAgendaGroup("G2").setFocus();
            agenda.getAgendaGroup("G1").setFocus();

            kieSession.fireAllRules();

            assertThat(list.size()).isEqualTo(2);
            assertThat(list.get(0)).isEqualTo("R2");
            assertThat(list.get(1)).isEqualTo("R1");
        } finally {
            kieSession.dispose();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
	@Timeout(10000)
    public void testSubnetworkSharingWith2SinksAfterLia(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-1656
        final String drl =
              "import " + X.class.getCanonicalName() + "\n" +
              "import " + Y.class.getCanonicalName() + "\n" +
              "import " + Z.class.getCanonicalName() + "\n" +
              "global java.util.List list" +
              "\n" +
              "rule R1 agenda-group \"G2\" when\n" +
              "     Z( id == 1)" +
              "     Z( id == 2)" +
              "Number( intValue == 0 ) from accumulate (\n" +
              "        X( $id : id )\n" +
              "        and $y : Y( parentId == $id )\n" +
              "    ;count($y))\n" +
              "then\n" +
              "    list.add(\"R1\");\n" +
              "end\n" +
              "\n" +
              "rule R2 agenda-group \"G1\" when\n" +
              "    Z( id == 1)" +
              "    Z( id == 2)" +
              "    Number( intValue < 1 ) from accumulate (\n" +
              "        X( $id : id )\n" +
              "        and $y : Y( parentId == $id )\n" +
              "    ;count($y))\n" +
              "then\n" +
              "    list.add(\"R2\");\n" +
              "end\n" +
              "\n" +
              "rule R3 agenda-group \"G1\" no-loop when\n" +
              "    Z( id == 1)" +
              "    Z( id == 2)" +
              "    $x : X( $id : id )\n" +
              "then\n" +
              "    modify($x) { setId($id + 1) };\n" +
              "end\n" +
              "rule R4 agenda-group \"G1\" no-loop when\n" +
              "    Z( id == 1)" +
              "    Z( id == 2)" +
              "    eval(true)\n" +
              "then\n" +
              "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-cep-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            kieSession.insert(new X(1));
            kieSession.insert(new Y(1));
            kieSession.insert(new Z(1));
            kieSession.insert(new Z(2));

            kieSession.insert(new X(3));
            kieSession.insert(new Y(3));

            final Agenda agenda = kieSession.getAgenda();
            agenda.getAgendaGroup("G2").setFocus();
            agenda.getAgendaGroup("G1").setFocus();

            kieSession.fireAllRules();

            assertThat(list.size()).isEqualTo(2);
            assertThat(list.get(0)).isEqualTo("R2");
            assertThat(list.get(1)).isEqualTo("R1");
        } finally {
            kieSession.dispose();
        }
    }

    public static class X {

        private int id;

        public X(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(final int id) {
            this.id = id;
        }
    }

    public static class Y {

        private final int parentId;

        public Y(final int parentId) {
            this.parentId = parentId;
        }

        public int getParentId() {
            return parentId;
        }
    }

    public static class Z {

        private int id;

        public Z(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(final int id) {
            this.id = id;
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void subnetworkSharingWith2SinksAndRightTupleDelete_shouldNotThrowNPE(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-7420
        testFromCollectInSubnetwork(kieBaseTestConfiguration, true);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void subnetworkNoSharingWith2SinksAndRightTupleDelete_shouldNotThrowNPE(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-7420
        testFromCollectInSubnetwork(kieBaseTestConfiguration, false);
    }

    private void testFromCollectInSubnetwork(KieBaseTestConfiguration kieBaseTestConfiguration, boolean nodeSharing) {
        String accConstraint = nodeSharing ? "" : "size > 0";
        final String drl =
                "import " + List.class.getCanonicalName() + ";\n" +
                "global java.util.List ints;\n" +
                "global java.util.List list;\n" +
                "rule R1\n" +
                "when\n" +
                "  not String()\n" +
                "  List() from collect (Integer() from ints)\n" +
                "  Boolean() // always doesn't match\n" +
                "then\n" +
                "  list.add(\"R1\");\n" +
                "end\n" +
                "\n" +
                "rule R2\n" +
                "when\n" +
                "  not String()\n" +
                "  List(" + accConstraint + ") from collect (Integer() from ints)\n" +
                " then\n" +
                "  list.add(\"R2\");\n" +
                "  insert(new String());\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();

        try {
            final List<String> list = new ArrayList<>();
            kieSession.setGlobal("list", list);
            kieSession.setGlobal("ints", asList(1,2));

            kieSession.fireAllRules();

            assertThat(list).containsExactly("R2");
        } finally {
            kieSession.dispose();
        }
    }
}
