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

package org.drools.compiler.integrationtests.session;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Move;
import org.drools.compiler.Person;
import org.drools.compiler.PersonFinal;
import org.drools.compiler.Pet;
import org.drools.compiler.Win;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class InsertTest extends CommonTestMethodBase {

    @Test
    public void testInsert() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "import org.drools.compiler.Person\n";
        drl += "import org.drools.compiler.Pet\n";
        drl += "import java.util.ArrayList\n";
        drl += "global java.util.List list\n";
        drl += "rule test\n";
        drl += "when\n";
        drl += "$person:Person()\n";
        drl += "$pets : ArrayList()\n";
        drl += "   from collect( \n";
        drl += "      Pet(\n";
        drl += "         ownerName == $person.name\n";
        drl += "      )\n";
        drl += "   )\n";
        drl += "then\n";
        drl += "  list.add( $person );\n";
        drl += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(drl);
        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("Toni");
        ksession.insert(p);
        ksession.insert(new Pet("Toni"));

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertSame(p, list.get(0));
    }

    @Test
    public void testInsertionOrder() {
        final KieBase kbase = loadKnowledgeBase("test_InsertionOrder.drl");

        KieSession ksession = createKnowledgeSession(kbase);
        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);
        ksession.insert(new Move(1, 2));
        ksession.insert(new Move(2, 3));

        final Win win2 = new Win(2);
        final Win win3 = new Win(3);

        ksession.fireAllRules();
        assertEquals(2, results.size());
        assertTrue(results.contains(win2));
        assertTrue(results.contains(win3));

        ksession.dispose();
        ksession = createKnowledgeSession(kbase);
        results = new ArrayList<>();
        ksession.setGlobal("results", results);
        // reverse the order of the inserts
        ksession.insert(new Move(2, 3));
        ksession.insert(new Move(1, 2));

        ksession.fireAllRules();
        assertEquals(2, results.size());
        assertTrue(results.contains(win2));
        assertTrue(results.contains(win3));
    }

    @Test
    public void testInsertFinalClassInstance() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_FinalClass.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final PersonFinal bob = new PersonFinal();
        bob.setName("bob");
        bob.setStatus(null);

        ksession.insert(bob);
        ksession.fireAllRules();
        assertEquals(1, list.size());
    }
}
