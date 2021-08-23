/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.Role;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SubnetworkTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SubnetworkTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
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

    @Test(timeout = 10000)
    public void testRightStagingOnSharedSubnetwork() {
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

    @Test(timeout = 10000)
    public void testUpdateOnSharedSubnetwork() {
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

            assertEquals(1, list.size());
            assertEquals(4, list.get(0).intValue());
        } finally {
            kieSession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testSubNeworkNotRemoveRightRemove() {
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

            assertEquals(1, list.size());
        } finally {
            kieSession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testSubNeworkNotRemoveLeftRemove() {
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
            assertEquals(0, list.size());
            list.clear();;

            kieSession.update(fh2, new Dimension(100, 100));
            kieSession.delete(fhg);
            kieSession.fireAllRules();
            assertEquals(0, list.size());


        } finally {
            kieSession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testSubNeworkQueryTest() {
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
            assertEquals(0, results.size() );

            kieSession.update(fh2, new Dimension(100, 100));
            kieSession.fireAllRules();

            results = kieSession.getQueryResults("q1", "go");
            assertEquals(1, results.size() );

            kieSession.update(fh1, new Dimension(100, 200));
            kieSession.fireAllRules();

            results = kieSession.getQueryResults("q1", "go");
            assertEquals(0, results.size() );

            kieSession.update(fh1, new Dimension(100, 100));
            kieSession.fireAllRules();

            results = kieSession.getQueryResults("q1", "go");
            assertEquals(1, results.size() );

        } finally {
            kieSession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testSubNetworks() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources("subnetwork-test", kieBaseTestConfiguration,
                                                                             "org/drools/compiler/integrationtests/test_SubNetworks.drl");
        final KieSession session = kieBase.newKieSession();
        session.dispose();
    }

    @Test(timeout = 10000)
    public void testSubnetworkSharingWith2SinksFromLia() {
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

            assertEquals(2, list.size());
            assertEquals("R2", list.get(0));
            assertEquals("R1", list.get(1));
        } finally {
            kieSession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testSubnetworkSharingWith2SinksAfterLia() {
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

            assertEquals(2, list.size());
            assertEquals("R2", list.get(0));
            assertEquals("R1", list.get(1));
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
}
