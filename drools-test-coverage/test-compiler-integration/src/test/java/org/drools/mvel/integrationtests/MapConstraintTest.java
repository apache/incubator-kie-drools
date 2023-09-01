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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.kiesession.audit.WorkingMemoryConsoleLogger;
import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.Pet;
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
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.KieSession;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class MapConstraintTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MapConstraintTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testMapAccess() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_MapAccess.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Map map = new HashMap();
        map.put("name", "Edson");
        map.put("surname", "Tirelli");
        map.put("age", 28);

        ksession.insert(map);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains(map)).isTrue();
    }

    @Test
    public void testMapAccessWithVariable() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_MapAccessWithVariable.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Map map = new HashMap();
        map.put("name", "Edson");
        map.put("surname", "Tirelli");
        map.put("age", "28");

        ksession.insert(map);
        ksession.insert("name");

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains(map)).isTrue();
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

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testMapNullConstraint() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_mapNullConstraints.drl");
        KieSession ksession = kbase.newKieSession();

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
        assertThat(aaf.getMatch().getRule().getName()).isEqualTo("1. home != null");
        aaf = arg.getAllValues().get(1);
        assertThat(aaf.getMatch().getRule().getName()).isEqualTo("2. not home == null");

        aaf = arg.getAllValues().get(2);
        assertThat(aaf.getMatch().getRule().getName()).isEqualTo("7. work == null");
        aaf = arg.getAllValues().get(3);
        assertThat(aaf.getMatch().getRule().getName()).isEqualTo("8. not work != null");
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new MapContainerBean());
        assertThat(ksession.fireAllRules()).isEqualTo(4);
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final Map<String, String> mark = new HashMap<>();
        mark.put("type", "Person");
        mark.put("name", "Mark");

        ksession.insert(mark);

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(0);

        final Map<String, String> bob = new HashMap<>();
        bob.put("type", "Person");
        bob.put("name", "Bob");

        ksession.insert(bob);

        rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @Test
    public void testListOfMaps() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_TestMapVariableRef.drl");
        KieSession ksession = kbase.newKieSession();
        final List<Map<String, Object>> list = new ArrayList<>();

        final Map mapOne = new HashMap<String, Object>();
        final Map mapTwo = new HashMap<String, Object>();

        mapOne.put("MSG", "testMessage");
        mapTwo.put("MSGTWO", "testMessage");

        list.add(mapOne);
        list.add(mapTwo);
        ksession.insert(list);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    public void testAccessingMapValues() throws Exception {
        String rule = "";
        rule += "package org.drools.mvel.compiler;\n";
        rule += "import org.drools.mvel.compiler.Pet;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "  when\n";
        rule += "    $pet: Pet()\n";
        rule += "    Pet( \n";
        rule += "      ownerName == $pet.attributes[\"key\"] \n";
        rule += "    )\n";
        rule += "  then\n";
        rule += "    System.out.println(\"hi pet\");\n";
        rule += "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
        KieSession session = kbase.newKieSession();
        assertThat(session).isNotNull();

        final Pet pet1 = new Pet("Toni");
        pet1.getAttributes().put("key", "value");
        final Pet pet2 = new Pet("Toni");

        session.insert(pet1);
        session.insert(pet2);

        session.fireAllRules();
    }

    @Test
    public void testMapOfMap() {
        // DROOLS-6599
        final String str =
                "package org.drools.compiler\n" +
                "import " + MapOfMapContainerBean.class.getCanonicalName() + ";\n" +
                "import java.util.Map\n" +
                "rule \"test\"\n" +
                "when\n" +
                "  MapOfMapContainerBean( map['key1']['key2'] == \"ok\" )\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        MapOfMapContainerBean bean = new MapOfMapContainerBean();
        Map<String, Map<String, String>> map = bean.getMap();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put("key2", "ok");
        map.put("key1", innerMap);

        ksession.insert(bean);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class MapOfMapContainerBean {
        private final Map<String, Map<String, String>> map = new HashMap<>();

        public Map<String, Map<String, String>> getMap() {
            return map;
        }
    }
}
