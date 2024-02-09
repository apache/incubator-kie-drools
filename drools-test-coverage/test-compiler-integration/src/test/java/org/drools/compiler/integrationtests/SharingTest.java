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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.base.base.ClassObjectType;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.drools.mvel.MVELConstraint;
import org.drools.testcoverage.common.model.FactWithList;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;

import static org.assertj.core.api.Assertions.assertThat;

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

        public static final NestedObj nestedObj = new NestedObj();
        public static final int FINAL_1 = 1;
        public static int nonFinal1 = 1;

        public static int return1() {
            return 1;
        }
    }

    public static class NestedObj {
        public static final int FINAL_1 = 1;
    }

    public static enum TestEnum {
        AAA,
        BBB,
        CCC;
    }

    @Test
    public void testDontShareAlphaWithStaticMethod() {
        // DROOLS-6418
        final String drl1 = "package c;\n" +
                            "import " + TestObject.class.getCanonicalName() + "\n" +
                            "import " + TestStaticUtils.class.getCanonicalName() + "\n" +
                            "rule R1 when\n" +
                            "  TestObject(value == 1)\n" +
                            "then\n" +
                            "end\n" +
                            "rule R2 when\n" +
                            "  TestObject(value == TestStaticUtils.return1())\n" + // return1() doesn't guarantee that it always returns 1
                            "then\n" +
                            "end\n" +
                            "rule R3 when\n" +
                            "  TestObject(value == 0 )\n" +
                            "then\n" +
                            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl1);

        ObjectTypeNode otn = getObjectTypeNode(kbase, TestObject.class);

        assertSinksSize(otn, 3); // Not shared
        assertHashableSinksSize(otn, 2);
        assertNonHashableConstraint(otn, "value == TestStaticUtils.return1()");

        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new TestObject(1));
            assertThat(kieSession.fireAllRules()).isEqualTo(2);
        } finally {
            kieSession.dispose();
        }
    }

    private ObjectTypeNode getObjectTypeNode(KieBase kbase, Class<?> factClass) {
        EntryPointNode epn = ((InternalKnowledgeBase) kbase).getRete().getEntryPointNodes().values().iterator().next();
        return epn.getObjectTypeNodes().get(new ClassObjectType(factClass));
    }

    private void assertSinksSize(ObjectTypeNode otn, int expected) {
        assertThat(otn.getSinks().length).isEqualTo(expected);
    }

    private void assertHashableSinksSize(ObjectTypeNode otn, int expected) {
        CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();

        if (expected == 0) {
            assertThat(sinkAdapter.getHashableSinks()).isNull();
        } else {
            assertThat(sinkAdapter.getHashableSinks().size()).isEqualTo(expected);
        }
    }

    private void assertNonHashableConstraint(ObjectTypeNode otn, String expected) {
        CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) otn.getObjectSinkPropagator();

        AlphaNode alpha = (AlphaNode) sinkAdapter.getOtherSinks().get(0);
        AlphaNodeFieldConstraint constraint = alpha.getConstraint();
        if (constraint instanceof MVELConstraint) {
            assertThat(((MVELConstraint) constraint).getExpression()).isEqualTo(expected);
        } else if (constraint instanceof LambdaConstraint) {
            assertThat(((LambdaConstraint) constraint).getPredicateInformation().getStringConstraint()).isEqualTo(expected);
        }
    }

    @Test
    public void testDontShareAlphaWithNonFinalField() {
        // DROOLS-6418
        final String drl = "package com.example;\n" +
                           "import " + TestObject.class.getCanonicalName() + "\n" +
                           "import " + TestStaticUtils.class.getCanonicalName() + "\n" +
                           "rule R1 when\n" +
                           "  TestObject(value == 1)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R2 when\n" +
                           "  TestObject(value == TestStaticUtils.nonFinal1)\n" + //  nonFinal1 doesn't guarantee that it always returns 1
                           "then\n" +
                           "end\n" +
                           "rule R3 when\n" +
                           "  TestObject(value == 0 )\n" +
                           "then\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl);

        ObjectTypeNode otn = getObjectTypeNode(kbase, TestObject.class);

        assertSinksSize(otn, 3); // Not shared
        assertHashableSinksSize(otn, 2);
        assertNonHashableConstraint(otn, "value == TestStaticUtils.nonFinal1");

        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new TestObject(1));
            assertThat(kieSession.fireAllRules()).isEqualTo(2);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testShareAlphaWithFinalField() {
        // DROOLS-6418
        final String drl = "package com.example;\n" +
                           "import " + TestObject.class.getCanonicalName() + "\n" +
                           "import " + TestStaticUtils.class.getCanonicalName() + "\n" +
                           "rule R1 when\n" +
                           "  TestObject(value == 1)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R2 when\n" +
                           "  TestObject(value == TestStaticUtils.FINAL_1)\n" + // FINAL_1 always returns 1
                           "then\n" +
                           "end\n" +
                           "rule R3 when\n" +
                           "  TestObject(value == 0 )\n" +
                           "then\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl);

        ObjectTypeNode otn = getObjectTypeNode(kbase, TestObject.class);

        if (kieBaseTestConfiguration.isExecutableModel()) {
            assertSinksSize(otn, 3); // exec-model doesn't share the final filed nodes. For improvement, see DROOLS-6485
            assertNonHashableConstraint(otn, "value == TestStaticUtils.FINAL_1");
        } else {
            assertSinksSize(otn, 2); // "value == 1" and "value == TestStaticUtils.FINAL_1" are shared
        }

        assertHashableSinksSize(otn, 2);

        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new TestObject(1));
            assertThat(kieSession.fireAllRules()).isEqualTo(2);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testShareAlphaWithNestedFinalField() {
        // DROOLS-6418
        final String drl = "package com.example;\n" +
                           "import " + TestObject.class.getCanonicalName() + "\n" +
                           "import " + TestStaticUtils.class.getCanonicalName() + "\n" +
                           "rule R1 when\n" +
                           "  TestObject(value == 1)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R2 when\n" +
                           "  TestObject(value == TestStaticUtils.nestedObj.FINAL_1)\n" + // FINAL_1 always returns 1
                           "then\n" +
                           "end\n" +
                           "rule R3 when\n" +
                           "  TestObject(value == 0 )\n" +
                           "then\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl);

        ObjectTypeNode otn = getObjectTypeNode(kbase, TestObject.class);

        // standard-drl cannot analyze nested final field. So not shared
        // exec-model doesn't share the final filed nodes. For improvement, see DROOLS-6485
        assertSinksSize(otn, 3);
        assertHashableSinksSize(otn, 2);
        assertNonHashableConstraint(otn, "value == TestStaticUtils.nestedObj.FINAL_1");

        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new TestObject(1));
            assertThat(kieSession.fireAllRules()).isEqualTo(2);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testShareAlphaWithEnum() {
        // DROOLS-6418
        final String drl = "package com.example;\n" +
                           "import " + TestObject.class.getCanonicalName() + "\n" +
                           "import " + TestEnum.class.getCanonicalName() + "\n" +
                           "rule R1 when\n" +
                           "  TestObject(testEnum == TestEnum.AAA)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R2 when\n" +
                           "  TestObject(testEnum == TestEnum.AAA)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R3 when\n" +
                           "  TestObject(testEnum == TestEnum.BBB)\n" +
                           "then\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl);

        ObjectTypeNode otn = getObjectTypeNode(kbase, TestObject.class);

        assertSinksSize(otn, 2); // shared
        assertHashableSinksSize(otn, 0); // not hash indexable

        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new TestObject(TestEnum.AAA));
            assertThat(kieSession.fireAllRules()).isEqualTo(2);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testDontShareAlphaWithBigDecimalConstructor() {
        // DROOLS-6418
        final String drl = "package com.example;\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "import " + BigDecimal.class.getCanonicalName() + "\n" +
                           "rule R1 when\n" +
                           "  Person(salary == 1)\n" +
                           "then\n" +
                           "end\n" +
                           "rule R2 when\n" +
                           "  Person(salary == new BigDecimal(\"1\"))\n" + // known constructor... always returns 1.
                           "then\n" +
                           "end\n" +
                           "rule R3 when\n" +
                           "  Person(salary == 0)\n" +
                           "then\n" +
                           "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("sharing-test", kieBaseTestConfiguration, drl);

        ObjectTypeNode otn = getObjectTypeNode(kbase, Person.class);

        assertSinksSize(otn, 3); // Not shared. For improvement, see DROOLS-6485
        assertHashableSinksSize(otn, 0); // Not indexed, see DROOLS-7085

        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new Person("John", 20, new BigDecimal("1")));
            assertThat(kieSession.fireAllRules()).isEqualTo(2);
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

        ObjectTypeNode otn = getObjectTypeNode(kbase, TestObject.class);
        assertSinksSize(otn, 2); // shared

        final KieSession kieSession = kbase.newKieSession();
        try {
            kieSession.insert(new TestObject(1));
            assertThat(kieSession.fireAllRules()).isEqualTo(2);
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

        ObjectTypeNode otn = getObjectTypeNode(kbase, FactWithList.class);
        assertSinksSize(otn, 2); // shared

        final KieSession kieSession = kbase.newKieSession();
        try {
            final FactWithList factWithList = new FactWithList("test");
            kieSession.insert(factWithList);

            assertThat(kieSession.fireAllRules()).isEqualTo(2);
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

        ObjectTypeNode otnA = getObjectTypeNode(kbase, A.class);
        assertSinksSize(otnA, 2);

        ObjectTypeNode otnB = getObjectTypeNode(kbase, B.class);
        assertSinksSize(otnB, 1);

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

            assertThat(list.size()).isEqualTo(2);
            assertThat(list.contains("R1")).isTrue();
            assertThat(list.contains("R2")).isTrue();
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

        private Integer value = -1;
        private TestEnum testEnum = TestEnum.AAA;

        public TestObject(Integer value) {
            this.value = value;
        }

        public TestObject(TestEnum testEnum) {
            this.testEnum = testEnum;
        }

        public Integer getValue() {
            return value;
        }

        public TestEnum getTestEnum() {
            return testEnum;
        }
    }
}
