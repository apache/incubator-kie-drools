/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.ArrayList;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBaseFactory;

public class AgendaGroupTest extends CommonTestMethodBase {

    @Test
    public void testClearActivationGroupCommand() {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(loadKnowledgePackagesFromString(createDRL()));
        KieSession ksession = kbase.newKieSession();

        ksession.setGlobal("list", new ArrayList<String>());
        ksession.getAgenda().getAgendaGroup("first-agenda").setFocus();
        ksession.getAgenda().getAgendaGroup("first-agenda").clear();
        ksession.fireAllRules();

        ArrayList<String> list = (ArrayList<String>)ksession.getGlobal("list");
        assertEquals(1, list.size());
        assertEquals("Rule without agenda group executed", list.get(0));
    }

    private String createDRL() {
        return "package org.kie.test\n" +
               "global java.util.List list\n" +
               "rule \"Rule in first agenda group\"\n" +
               "agenda-group \"first-agenda\"\n" +
               "salience 10\n" +
               "when\n" +
               "then\n" +
               "list.add(\"Rule in first agenda group executed\");\n" +
               "end\n" +
               "rule \"Rule without agenda group\"\n" +
               "salience 100\n" +
               "when\n" +
               "then\n" +
               "list.add(\"Rule without agenda group executed\");\n" +
               "end\n";
    }
}