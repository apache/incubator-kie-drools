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

package org.drools.compiler.integrationtests;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.compiler.Address;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.Pet;
import org.drools.core.WorkingMemory;
import org.drools.core.audit.WorkingMemoryConsoleLogger;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;

public class MapConstraintTest extends CommonTestMethodBase {

    @Test
    public void testMapAccess() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_MapAccess.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Map map = new HashMap();
        map.put("name", "Edson");
        map.put("surname", "Tirelli");
        map.put("age", "28");

        ksession.insert(map);

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains(map));
    }

    @Test
    public void testMapAccessWithVariable() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_MapAccessWithVariable.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Map map = new HashMap();
        map.put("name", "Edson");
        map.put("surname", "Tirelli");
        map.put("age", "28");

        ksession.insert(map);
        ksession.insert("name");

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains(map));
    }

    // Drools does not support variables inside bindings yet... but we should...
    @Test
    public void testMapAccessWithVariable2() {
        final String str = "package org.drools.compiler;\n" +
                "import java.util.Map;\n" +
                "rule \"map access with variable\"\n" +
                "    when\n" +
                "        $key : String( )\n" +
                "        $p1 : Person( name == 'Bob', namedAddresses[$key] != null, $na : namedAddresses[$key] )\n" +
                "        $p2 : Person( name == 'Mark', namedAddresses[$key] == $na )\n" +
                "    then\n" +
                "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        Assert.assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testMapNullConstraint() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_mapNullConstraints.drl");
        final KieSession ksession = createKnowledgeSession(kbase);

        final org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
        ksession.addEventListener(ael);
        new WorkingMemoryConsoleLogger((WorkingMemory) ksession);

        final Map addresses = new HashMap();
        addresses.put("home", new Address("home street"));
        final Person bob = new Person("Bob");
        bob.setNamedAddresses(addresses);

        ksession.insert(bob);
        ksession.fireAllRules();

        final ArgumentCaptor<AfterMatchFiredEvent> arg = ArgumentCaptor.forClass(org.kie.api.event.rule.AfterMatchFiredEvent.class);
        verify(ael, times(4)).afterMatchFired(arg.capture());
        org.kie.api.event.rule.AfterMatchFiredEvent aaf = arg.getAllValues().get(0);
        assertThat(aaf.getMatch().getRule().getName(), is("1. home != null"));
        aaf = arg.getAllValues().get(1);
        assertThat(aaf.getMatch().getRule().getName(), is("2. not home == null"));

        aaf = arg.getAllValues().get(2);
        assertThat(aaf.getMatch().getRule().getName(), is("7. work == null"));
        aaf = arg.getAllValues().get(3);
        assertThat(aaf.getMatch().getRule().getName(), is("8. not work != null"));
    }

    @Test
    public void testMapAccessorWithPrimitiveKey() {
        final String str = "package com.sample\n" +
                "import " + MapContainerBean.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "  MapContainerBean( map[1] == \"one\" )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  MapContainerBean( map[1+1] == \"two\" )\n" +
                "then\n" +
                "end\n" +
                "rule R3 when\n" +
                "  MapContainerBean( map[this.get3()] == \"three\" )\n" +
                "then\n" +
                "end\n" +
                "rule R4 when\n" +
                "  MapContainerBean( map[4] == null )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new MapContainerBean());
        assertEquals(4, ksession.fireAllRules());
        ksession.dispose();
    }

    public static class MapContainerBean {
        private final Map<Integer, String> map = new HashMap<>();

        MapContainerBean() {
            map.put( 1, "one" );
            map.put( 2, "two" );
            map.put( 3, "three" );
        }

        public Map<Integer, String> getMap() {
            return map;
        }

        public int get3() {
            return 3;
        }
    }

    @Test
    public void testMapModel() {
        final String str = "package org.drools.compiler\n" +
                "import java.util.Map\n" +
                "rule \"test\"\n" +
                "when\n" +
                "    Map( type == \"Person\", name == \"Bob\" );\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Map<String, String> mark = new HashMap<>();
        mark.put("type", "Person");
        mark.put("name", "Mark");

        ksession.insert(mark);

        int rules = ksession.fireAllRules();
        assertEquals(0, rules);

        final Map<String, String> bob = new HashMap<>();
        bob.put("type", "Person");
        bob.put("name", "Bob");

        ksession.insert(bob);

        rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testListOfMaps() {
        final KieBase kbase = loadKnowledgeBase("test_TestMapVariableRef.drl");

        final KieSession ksession = createKnowledgeSession(kbase);
        final List<Map<String, Object>> list = new ArrayList<>();

        final Map mapOne = new HashMap<String, Object>();
        final Map mapTwo = new HashMap<String, Object>();

        mapOne.put("MSG", "testMessage");
        mapTwo.put("MSGTWO", "testMessage");

        list.add(mapOne);
        list.add(mapTwo);
        ksession.insert(list);
        ksession.fireAllRules();

        assertEquals(3, list.size());
    }

    @Test
    public void testAccessingMapValues() throws Exception {
        String rule = "";
        rule += "package org.drools.compiler;\n";
        rule += "import org.drools.compiler.Pet;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "  when\n";
        rule += "    $pet: Pet()\n";
        rule += "    Pet( \n";
        rule += "      ownerName == $pet.attributes[\"key\"] \n";
        rule += "    )\n";
        rule += "  then\n";
        rule += "    System.out.println(\"hi pet\");\n";
        rule += "end";

        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(rule));
        final KieSession session = createKnowledgeSession(kbase);
        assertNotNull(session);

        final Pet pet1 = new Pet("Toni");
        pet1.getAttributes().put("key", "value");
        final Pet pet2 = new Pet("Toni");

        session.insert(pet1);
        session.insert(pet2);

        session.fireAllRules();
    }
}
