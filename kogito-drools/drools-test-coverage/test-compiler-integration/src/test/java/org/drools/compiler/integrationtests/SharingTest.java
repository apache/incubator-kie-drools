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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.FactWithList;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class SharingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SharingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    public static class TestStaticUtils {

        public static int return1() {
            return 1;
        }
    }

    @Test
    public void testShouldAlphaShareBecauseSameConstantDespiteDifferentSyntax() {
        // DROOLS-1404
        final String drl1 = "package c;\n" +
                "import " + TestObject.class.getCanonicalName() + "\n" +
                "rule fileArule1 when\n" +
                "  TestObject(value == 1)\n" +
                "then\n" +
                "end\n" +
                "";
        final String drl2 = "package iTzXzx;\n" + // <<- keep the different package
                "import " + TestObject.class.getCanonicalName() + "\n" +
                "import " + TestStaticUtils.class.getCanonicalName() + "\n" +
                "rule fileBrule1 when\n" +
                "  TestObject(value == TestStaticUtils.return1() )\n" +
                "then\n" +
                "end\n" +
                "rule fileBrule2 when\n" + // <<- keep this rule
                "  TestObject(value == 0 )\n" +
                "then\n" +
                "end\n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl1, drl2);
        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new TestObject(1));
            assertEquals(2, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testShouldAlphaShareNotEqualsInDifferentPackages() {
        // DROOLS-1404
        final String drl1 = "package c;\n" +
                "import " + TestObject.class.getCanonicalName() + "\n" +
                "rule fileArule1 when\n" +
                "  TestObject(value >= 1 )\n" +
                "then\n" +
                "end\n" +
                "";
        final String drl2 = "package iTzXzx;\n" + // <<- keep the different package
                "import " + TestObject.class.getCanonicalName() + "\n" +
                "rule fileBrule1 when\n" +
                "  TestObject(value >= 1 )\n" +
                "then\n" +
                "end\n" +
                "rule fileBrule2 when\n" + // <<- keep this rule
                "  TestObject(value >= 2 )\n" +
                "then\n" +
                "end\n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl1, drl2);
        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new TestObject(1));
            assertEquals(2, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testShouldAlphaShareNotEqualsInDifferentPackages2() {
        // DROOLS-1404
        final String drl1 = "package c;\n" +
                "import " + FactWithList.class.getCanonicalName() + "\n" +
                "\n" +
                "rule fileArule1 when\n" +
                "  FactWithList(items contains \"test\")\n" +
                "then\n" +
                "end\n" +
                "";
        final String drl2 = "package iTzXzx;\n" + // <<- keep the different package
                "import " + FactWithList.class.getCanonicalName() + "\n" +
                "rule fileBrule1 when\n" +
                "  FactWithList(items contains \"test\")\n" +
                "then\n" +
                "end\n" +
                "rule fileBrule2 when\n" + // <<- keep this rule
                "  FactWithList(items contains \"testtest\")\n" +
                "then\n" +
                "end\n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl1, drl2);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final FactWithList factWithList = new FactWithList("test");
            kieSession.insert(factWithList);

            assertEquals(2, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testSubnetworkSharing() {
        final String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "global java.util.List list" +
                        "\n" +
                        "rule R1 agenda-group \"G2\" when\n" +
                        "    Number( intValue < 1 ) from accumulate (\n" +
                        "        A( $id : id )\n" +
                        "        and $b : B( parentId == $id )\n" +
                        "    ;count($b))\n" +
                        "then\n" +
                        "    list.add(\"R1\");\n" +
                        "end\n" +
                        "\n" +
                        "rule R2 agenda-group \"G1\" when\n" +
                        "    Number( intValue < 1 ) from accumulate (\n" +
                        "        A( $id : id )\n" +
                        "        and $b : B( parentId == $id )\n" +
                        "\n" +
                        "    ;count($b))\n" +
                        "then\n" +
                        "    list.add(\"R2\");\n" +
                        "end\n" +
                        "\n" +
                        "rule R3 agenda-group \"G1\" no-loop when\n" +
                        "    $a : A( $id : id )\n" +
                        "then\n" +
                        "    modify($a) { setId($id + 1) };\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            kieSession.insert(new A(1));
            kieSession.insert(new B(1));

            final Agenda agenda = kieSession.getAgenda();
            agenda.getAgendaGroup("G2").setFocus();
            agenda.getAgendaGroup("G1").setFocus();

            kieSession.fireAllRules();

            assertEquals(2, list.size());
            assertTrue(list.contains("R1"));
            assertTrue(list.contains("R2"));
        } finally {
            kieSession.dispose();
        }
    }

    public static class A {

        private int id;

        public A(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(final int id) {
            this.id = id;
        }
    }

    public static class B {

        private final int parentId;

        public B(final int parentId) {
            this.parentId = parentId;
        }

        public int getParentId() {
            return parentId;
        }
    }

    public static class TestObject {

        private final Integer value;

        public TestObject(final Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

    }
}
