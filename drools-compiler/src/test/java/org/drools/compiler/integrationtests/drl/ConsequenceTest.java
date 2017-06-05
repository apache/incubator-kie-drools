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

package org.drools.compiler.integrationtests.drl;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.Pet;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class ConsequenceTest extends CommonTestMethodBase {

    @Test
    public void testConsequenceException() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_ConsequenceException.drl");
        final KieSession ksession = kbase.newKieSession();

        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);

        try {
            ksession.fireAllRules();
            fail("Should throw an Exception from the Consequence");
        } catch (final org.kie.api.runtime.rule.ConsequenceException e) {
            assertEquals("Throw Consequence Exception",
                    e.getMatch().getRule().getName());
            assertEquals("this should throw an exception",
                    e.getCause().getMessage());
        }
    }

    @Test
    public void testConsequenceBuilderException() throws Exception {
        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add(ResourceFactory.newClassPathResource("test_ConsequenceBuilderException.drl", getClass()),
                ResourceType.DRL);

        assertTrue(builder.hasErrors());
    }

    @Test
    public void testMetaConsequence() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_MetaConsequence.drl"));
        KieSession session = createKnowledgeSession(kbase);
        List results = new ArrayList();
        session.setGlobal("results", results);

        session.insert(new Person("Michael"));

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session,
                true);
        results = (List) session.getGlobal("results");

        session.fireAllRules();
        assertEquals(2, results.size());
        assertEquals("bar", results.get(0));
        assertEquals("bar2", results.get(1));
    }

    // following test depends on MVEL: http://jira.codehaus.org/browse/MVEL-212
    @Test
    public void testMVELConsequenceUsingFactConstructors() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "import org.drools.compiler.Person\n";
        drl += "global org.drools.core.runtime.StatefulKnowledgeSession ksession\n";
        drl += "rule test dialect 'mvel'\n";
        drl += "when\n";
        drl += "    $person:Person( name == 'mark' )\n";
        drl += "then\n";
        drl += "    // below constructor for Person does not exist\n";
        drl += "    Person p = new Person( 'bob', 30, 555 )\n";
        drl += "    ksession.update(ksession.getFactHandle($person), new Person('bob', 30, 999, 453, 534, 534, 32))\n";
        drl += "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(drl)), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testMVELConsequenceWithMapsAndArrays() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "import java.util.ArrayList\n";
        rule += "import java.util.HashMap\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Test Rule\"\n";
        rule += "    dialect \"mvel\"";
        rule += "when\n";
        rule += "then\n";
        rule += "    m = new HashMap();\n";
        rule += "    l = new ArrayList();\n";
        rule += "    l.add(\"first\");\n";
        rule += "    m.put(\"content\", l);\n";
        rule += "    System.out.println(((ArrayList)m[\"content\"])[0]);\n";
        rule += "    list.add(((ArrayList)m[\"content\"])[0]);\n";
        rule += "end";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(rule));
        KieSession session = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        session.setGlobal("list", list);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();

        assertEquals(1, ((List) session.getGlobal("list")).size());
        assertEquals("first", ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testMVELConsequenceWithoutSemiColon1() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "import org.drools.compiler.Person\n";
        drl += "import org.drools.compiler.Pet\n";
        drl += "rule test dialect 'mvel'\n";
        drl += "when\n";
        drl += "    $person:Person()\n";
        drl += "    $pet:Pet()\n";
        drl += "then\n";
        drl += "    delete($person) // some comment\n";
        drl += "    delete($pet) // another comment\n";
        drl += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(drl);

        final KieSession ksession = createKnowledgeSession(kbase);

        // create working memory mock listener
        final RuleRuntimeEventListener wml = Mockito.mock(RuleRuntimeEventListener.class);

        ksession.addEventListener(wml);

        final FactHandle personFH = ksession.insert(new Person("Toni"));
        final FactHandle petFH = ksession.insert(new Pet("Toni"));

        final int fired = ksession.fireAllRules();
        assertEquals(1,
                fired);

        // capture the arguments and check that the retracts happened
        final ArgumentCaptor<ObjectDeletedEvent> retracts = ArgumentCaptor.forClass(ObjectDeletedEvent.class);
        verify(wml, times(2)).objectDeleted(retracts.capture());
        final List<ObjectDeletedEvent> values = retracts.getAllValues();
        assertThat(values.get(0).getFactHandle(), is(personFH));
        assertThat(values.get(1).getFactHandle(), is(petFH));
    }
}
