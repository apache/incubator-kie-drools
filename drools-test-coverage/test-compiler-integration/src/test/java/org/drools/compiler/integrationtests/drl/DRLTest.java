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
package org.drools.compiler.integrationtests.drl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.testcoverage.common.model.A;
import org.drools.testcoverage.common.model.B;
import org.drools.testcoverage.common.model.C;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.D;
import org.drools.testcoverage.common.model.E;
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
import org.kie.api.builder.Message;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DRLTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DRLTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testDuplicateRuleName() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "rule R when\n" +
                "then\n" +
                "end\n" +
                "rule R when\n" +
                "then\n" +
                "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testEmptyRule() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "global java.util.List list\n" +
                "rule \"empty lhs1\"\n" +
                "    when\n" +
                "    then\n" +
                "        list.add(\"fired1\");\n" +
                "end    \n" +
                "rule \"empty lhs2\"\n" +
                "    when\n" +
                "    then\n" +
                "        list.add(\"fired2\");\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("drl-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();

            assertThat(list.contains("fired1")).isTrue();
            assertThat(list.contains("fired2")).isTrue();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testRuleMetaAttributes() {
        final String drl =
            "package org.drools.compiler.integrationtests.drl;\n" +
            "rule \"test meta attributes\"\n" +
            "    @id(1234 ) @author(  john_doe  ) @text(\"It's an escaped\\\" string\"  )\n" +
            "when\n" +
            "then\n" +
            "    // some comment\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("drl-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Rule rule = kbase.getRule("org.drools.compiler.integrationtests.drl", "test meta attributes");

            assertThat(rule).isNotNull();
            assertThat(rule.getMetaData().get("id")).isEqualTo(1234);
            assertThat(rule.getMetaData().get("author")).isEqualTo("john_doe");
            assertThat(rule.getMetaData().get("text")).isEqualTo("It's an escaped\" string");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testWithInvalidRule() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "//error 1) missing a person import\n" +
                "\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"not rule test\"\n" +
                "    when\n" +
                "        // error 2) incorrect field\n" +
                "        $person : Person( $likes:likeypooh )\n" +
                "        not Cheese( type == $likes )\n" +
                "    then\n" +
                "        list.add( $person );\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText).doesNotContain("");
    }

    @Test
    public void testWithInvalidRule2() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "\n" +
                "\n" +
                "rule \"not rule test\"\n" +
                "when\n" +
                "    foo\n" +
                "then\n" +
                "    System.err.println(\"hey\");\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText).doesNotContain("");
    }

    @Test
    public void testDuplicateVariableBinding() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.Map results;\n" +
                "\n" +
                "rule \"Duplicate Variable testing\"\n" +
                " when\n" +
                "   // there should be no problem since each variable \n" +
                "   // is in a different logical branch\n" +
                "   Cheese( $type : type == \"stilton\", $price : price ) or\n" +
                "   Cheese( $type : type == \"brie\", $price : price )\n" +
                " then\n" +
                "   results.put( $type, new Integer( $price ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Duplicate Variable testing 2\"\n" +
                " when\n" +
                "   // there should be no problem since each variable \n" +
                "   // is in a different logical branch\n" +
                "   $cheese : Cheese( type == \"stilton\", $price : price ) or\n" +
                "   $cheese : Cheese( type == \"brie\", $price : price )\n" +
                " then\n" +
                "   results.put( $cheese, new Integer( $price ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Duplicate Variable testing 3\"\n" +
                " when\n" +
                "   // there should be no problem since each variable \n" +
                "   // is in a different logical branch\n" +
                "   Cheese( $type : type == \"stilton\", $price : price ) or\n" +
                "   ( Cheese( $type : type == \"brie\", $price : price ) and Person( name == \"bob\", likes == $type ) )\n" +
                " then\n" +
                "   results.put( \"test3\"+$type, new Integer( $price ) );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("drl-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Map result = new HashMap();
            ksession.setGlobal("results", result);

            final Cheese stilton = new Cheese("stilton", 20);
            final Cheese brie = new Cheese("brie", 10);

            ksession.insert(stilton);
            ksession.insert(brie);

            ksession.fireAllRules();
            assertThat(result.size()).isEqualTo(5);
            assertThat(((Integer) result.get(stilton.getType())).intValue()).isEqualTo(stilton.getPrice());
            assertThat(((Integer) result.get(brie.getType())).intValue()).isEqualTo(brie.getPrice());

            assertThat(((Integer) result.get(stilton)).intValue()).isEqualTo(stilton.getPrice());
            assertThat(((Integer) result.get(brie)).intValue()).isEqualTo(brie.getPrice());

            assertThat(((Integer) result.get("test3" + stilton.getType())).intValue()).isEqualTo(stilton.getPrice());

            final Person bob = new Person("bob");
            bob.setLikes(brie.getType());
            ksession.insert(bob);
            ksession.fireAllRules();

            assertThat(result.size()).isEqualTo(6);
            assertThat(((Integer) result.get("test3" + brie.getType())).intValue()).isEqualTo(brie.getPrice());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDeclarationUsage() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"test declaration\"\n" +
                "when\n" +
                "    Cheese( type == $likes )\n" +
                "    Person( $likes : likes );\n" +
                "then\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText).doesNotContain("");
    }

    @Test
    public void testDeclarationNonExistingField() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"test declaration of non existing field\"\n" +
                "when\n" +
                "    Person( $likes : likes, $nef : nonExistingField )\n" +
                "    Cheese( type == $likes ) \n" +
                "then\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText).doesNotContain("");
    }

    @Test
    public void testDRLWithoutPackageDeclaration() throws Exception {

        final String drl = "global java.util.List results\n" +
                "\n" +
                "function boolean test( Object o1, Object o2 ) {\n" +
                "    return o1.equals(o2);\n" +
                "}\n" +
                "\n" +
                "declare Person\n" +
                "    name : String @key\n" +
                "    age : int\n" +
                "end    \n" +
                "\n" +
                "rule \"TestRule\"\n" +
                "when\n" +
                "    $p : Person( name == \"Bob\" );\n" +
                "then\n" +
                "    results.add( $p );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("drl-test", kieBaseTestConfiguration, drl);

        // no package defined, so it is set to the default
        final FactType factType = kbase.getFactType("defaultpkg", "Person");
        assertThat(factType).isNotNull();
        final Object bob = factType.newInstance();
        factType.set(bob, "name", "Bob");
        factType.set(bob, "age", 30);

        final KieSession session = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            session.setGlobal("results", results);

            session.insert(bob);
            session.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isEqualTo(bob);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testPackageNameOfTheBeast() throws Exception {
        // JBRULES-2749 Various rules stop firing when they are in unlucky packagename and there is a function declared

        final String ruleFileContent1 = "package org.drools.integrationtests;\n" +
                "function void myFunction() {\n" +
                "}\n" +
                "declare MyDeclaredType\n" +
                "  someProperty: boolean\n" +
                "end";
        final String ruleFileContent2 = "package de.something;\n" + // FAILS
                "import org.drools.integrationtests.*;\n" +
                "rule \"CheckMyDeclaredType\"\n" +
                "  when\n" +
                "    MyDeclaredType()\n" +
                "  then\n" +
                "    insertLogical(\"THIS-IS-MY-MARKER-STRING\");\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("drl-test", kieBaseTestConfiguration, ruleFileContent1, ruleFileContent2);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactType myDeclaredFactType = kbase.getFactType("org.drools.integrationtests", "MyDeclaredType");
            final Object myDeclaredFactInstance = myDeclaredFactType.newInstance();
            ksession.insert(myDeclaredFactInstance);

            final int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testLargeDRL() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration,
                                                                             "org/drools/compiler/integrationtests/largedrl.drl");
        final KieSession kieSession = kieBase.newKieSession();
        try {
            kieSession.insert(new A(100000));
            kieSession.insert(new B(100001));
            kieSession.insert(new C(100002));
            kieSession.insert(new D(100003));
            kieSession.insert(new E(100004));

            assertThat(kieSession.fireAllRules()).isEqualTo(50);
        } finally {
            kieSession.dispose();
        }
    }
}
