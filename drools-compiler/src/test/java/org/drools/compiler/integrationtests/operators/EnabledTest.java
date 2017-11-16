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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Foo;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mockito.ArgumentCaptor;

public class EnabledTest extends CommonTestMethodBase {

    @Test
    public void testEnabledExpression() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_enabledExpression.drl"));
        final KieSession session = createKnowledgeSession(kbase);
        List results = new ArrayList();
        session.setGlobal("results", results);

        session.insert(new Person("Michael"));

        results = (List) session.getGlobal("results");

        session.fireAllRules();
        assertEquals(3, results.size());
        assertTrue(results.contains("1"));
        assertTrue(results.contains("2"));
        assertTrue(results.contains("3"));
    }

    @Test
    public void testEnabledExpression2() {
        final String drl = "import " + Foo.class.getName() + ";\n" +
                "rule R1\n" +
                "    enabled( rule.name == $f.id )" +
                "when\n" +
                "   $f : Foo()\n" +
                "then end\n" +
                "rule R2\n" +
                "when\n" +
                "   Foo( id == \"R2\" )\n" +
                "then end\n";

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-enabled", "1.0.0");
        final KieModule km = createAndDeployJar(ks, releaseId1, drl);

        // Create a session and fire rules
        final KieContainer kc = ks.newKieContainer(km.getReleaseId());

        AgendaEventListener ael = mock(AgendaEventListener.class);
        KieSession ksession = kc.newKieSession();
        ksession.addEventListener(ael);
        ksession.insert(new Foo("R1", null));
        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();

        ArgumentCaptor<AfterMatchFiredEvent> event = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
        verify(ael).afterMatchFired(event.capture());
        assertEquals("R1", event.getValue().getMatch().getRule().getName());

        ael = mock(AgendaEventListener.class);
        ksession = kc.newKieSession();
        ksession.addEventListener(ael);
        ksession.insert(new Foo("R2", null));
        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();

        event = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
        verify(ael).afterMatchFired(event.capture());
        assertEquals("R2", event.getValue().getMatch().getRule().getName());
    }

}
