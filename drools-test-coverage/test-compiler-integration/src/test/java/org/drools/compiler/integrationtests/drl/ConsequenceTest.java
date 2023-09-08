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
import java.util.List;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Pet;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ConsequenceTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ConsequenceTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testConsequenceException() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "rule \"Throw Consequence Exception\"\n" +
                "    when\n" +
                "        cheese : Cheese( )\n" +
                "    then\n" +
                "        throw new Exception( \"this should throw an exception\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("consequence-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Cheese brie = new Cheese("brie", 12);
            ksession.insert(brie);

            try {
                ksession.fireAllRules();
                fail("Should throw an Exception from the Consequence");
            } catch (final org.kie.api.runtime.rule.ConsequenceException e) {
                assertThat(e.getMatch().getRule().getName()).isEqualTo("Throw Consequence Exception");
                assertThat(e.getCause().getMessage()).isEqualTo("this should throw an exception");
            }
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testConsequenceBuilderException() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "global java.util.List results;\n" +
                "rule \"error compiling consequence\"\n" +
                "    when\n" +
                "    then\n" +
                "        // this must generate a compile error, but not NPE\n" +
                "        results.add(message without quotes);\n" +
                "end\n" +
                "rule \"another test\"\n" +
                "    salience 10\n" +
                "\n" +
                "   when\n" +
                "    eval( true ) \n" +
                "then\n" +
                "    System.out.println(1);\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                                                                    false,
                                                                    drl);
        assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testMetaConsequence() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "global java.util.List results;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"Test Consequence\"\n" +
                "    @foo(bar)\n" +
                "    @foo2(bar2)\n" +
                "    @ruleID(1234)\n" +
                "    @parentRuleID(1234)\n" +
                "    @dateActive(12/1/08)\n" +
                "    @price(123.00)\n" +
                "    @errorMSG(Stop)\n" +
                "    @userMSG(\"Please Stop\")\n" +
                "  when\n" +
                "    Person(name == \"Michael\")\n" +
                "  then\n" +
                "    results.add( drools.getRule().getMetaData().get(\"foo\"));\n" +
                "    results.add( drools.getRule().getMetaData().get(\"foo2\"));\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("consequence-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            List results = new ArrayList();
            session.setGlobal("results", results);

            session.insert(new Person("Michael"));
            results = (List) session.getGlobal("results");

            session.fireAllRules();
            assertThat(results.size()).isEqualTo(2);
            assertThat(results.get(0)).isEqualTo("bar");
            assertThat(results.get(1)).isEqualTo("bar2");
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testMVELConsequenceWithMapsAndArrays() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
            "import java.util.ArrayList\n" +
            "import java.util.HashMap\n" +
            "global java.util.List list\n" +
            "rule \"Test Rule\"\n" +
            "    dialect \"mvel\"" +
            "when\n" +
            "then\n" +
            "    m = new HashMap();\n" +
            "    l = new ArrayList();\n" +
            "    l.add(\"first\");\n" +
            "    m.put(\"content\", l);\n" +
            "    System.out.println(((ArrayList)m[\"content\"])[0]);\n" +
            "    list.add(((ArrayList)m[\"content\"])[0]);\n" +
            "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("consequence-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            session.setGlobal("list", list);
            session.fireAllRules();

            assertThat(((List) session.getGlobal("list")).size()).isEqualTo(1);
            assertThat(((List) session.getGlobal("list")).get(0)).isEqualTo("first");
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testMVELConsequenceWithoutSemiColon1() {
        final String drl =
            "package prg.drools.compiler.integrationtests.drl;\n" +
            "import " + Person.class.getCanonicalName() + ";\n" +
            "import " + Pet.class.getCanonicalName() + ";\n" +
            "rule test dialect 'mvel'\n" +
            "when\n" +
            "    $person:Person()\n" +
            "    $pet:Pet()\n" +
            "then\n" +
            "    delete($person) // some comment\n" +
            "    delete($pet) // another comment\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("consequence-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            // create working memory mock listener
            final RuleRuntimeEventListener wml = Mockito.mock(RuleRuntimeEventListener.class);

            ksession.addEventListener(wml);

            final FactHandle personFH = ksession.insert(new Person("Toni"));
            final FactHandle petFH = ksession.insert(new Pet("Toni"));

            final int fired = ksession.fireAllRules();
            assertThat(fired).isEqualTo(1);

            // capture the arguments and check that the retracts happened
            final ArgumentCaptor<ObjectDeletedEvent> retracts = ArgumentCaptor.forClass(ObjectDeletedEvent.class);
            verify(wml, times(2)).objectDeleted(retracts.capture());
            final List<ObjectDeletedEvent> values = retracts.getAllValues();
            assertThat(values.get(0).getFactHandle()).isEqualTo(personFH);
            assertThat(values.get(1).getFactHandle()).isEqualTo(petFH);
        } finally {
            ksession.dispose();
        }
    }
}
