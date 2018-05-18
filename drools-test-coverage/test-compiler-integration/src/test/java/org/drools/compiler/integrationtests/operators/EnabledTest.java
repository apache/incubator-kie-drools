/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.MyFact;
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
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class EnabledTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EnabledTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testEnabledExpression() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Test enabled expression 1\"\n" +
                "    @ruleID(1234)\n" +
                "    // arbitrary expression using a rule metadata\n" +
                "    enabled ( rule.metaData[\"ruleID\"] == \"1234\" )\n" +
                "  when\n" +
                "    Person(name == \"Michael\")\n" +
                "  then\n" +
                "    results.add( \"1\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Test enabled expression 2\"\n" +
                "    @ruleID(1234)\n" +
                "    // using bound variables\n" +
                "    enabled ( \"Michael\".equals( $name ) )\n" +
                "  when\n" +
                "    Person( $name : name )\n" +
                "  then\n" +
                "    results.add( \"2\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Test enabled expression 3\"\n" +
                "    @ruleID(1234)\n" +
                "    // using simple expressions\n" +
                "    enabled ( 1 + 1 == 2 )\n" +
                "  when\n" +
                "    Person( $name : name )\n" +
                "  then\n" +
                "    results.add( \"3\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Test enabled expression 4\"\n" +
                "    @ruleID(1234)\n" +
                "    // using a false expression\n" +
                "    enabled ( 1 + 1 == 5 )\n" +
                "  when\n" +
                "    Person( $name : name )\n" +
                "  then\n" +
                "    results.add( \"4\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("enabled-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);

        final KieSession session = kbase.newKieSession();
        try {
            List results = new ArrayList();
            session.setGlobal("results", results);

            session.insert(new Person("Michael"));

            results = (List) session.getGlobal("results");

            session.fireAllRules();
            assertEquals(3, results.size());
            assertTrue(results.contains("1"));
            assertTrue(results.contains("2"));
            assertTrue(results.contains("3"));
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testEnabledExpression2() {
        final String drl = "import " + MyFact.class.getName() + ";\n" +
                "rule R1\n" +
                "    enabled( rule.name == $f.name )" +
                "when\n" +
                "   $f : MyFact()\n" +
                "then end\n" +
                "rule R2\n" +
                "when\n" +
                "   MyFact( name == \"R2\" )\n" +
                "then end\n";

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-enabled", "1.0.0");
        final KieModule km = KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, drl);

        // Create a session and fire rules
        final KieContainer kc = ks.newKieContainer(km.getReleaseId());

        AgendaEventListener ael = mock(AgendaEventListener.class);
        KieSession ksession = kc.newKieSession();
        try {
            ksession.addEventListener(ael);
            ksession.insert(new MyFact("R1", null));
            assertEquals(1, ksession.fireAllRules());
            ksession.dispose();

            ArgumentCaptor<AfterMatchFiredEvent> event = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            verify(ael).afterMatchFired(event.capture());
            assertEquals("R1", event.getValue().getMatch().getRule().getName());

            ael = mock(AgendaEventListener.class);
            ksession.dispose();
            ksession = kc.newKieSession();
            ksession.addEventListener(ael);
            ksession.insert(new MyFact("R2", null));
            assertEquals(1, ksession.fireAllRules());
            ksession.dispose();

            event = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            verify(ael).afterMatchFired(event.capture());
            assertEquals("R2", event.getValue().getMatch().getRule().getName());
        } finally {
            ksession.dispose();
        }
    }

}
